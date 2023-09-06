package com.assessment.casinoapi.service;


import com.assessment.casinoapi.exception.InsufficientFundsException;
import com.assessment.casinoapi.exception.ResourceNotFoundException;
import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import com.assessment.casinoapi.respository.PlayerRepository;
import com.assessment.casinoapi.respository.TransactionRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link CasinoService} interface.
 */
@Slf4j
@Service
public class CasinoServiceImpl implements CasinoService {
    private final PlayerRepository playerRepository;
    private final TransactionRepository transactionRepository;

    public CasinoServiceImpl(final PlayerRepository playerRepository, final TransactionRepository transactionRepository) {
        this.playerRepository = playerRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Get a Player by ID
     * @param playerId the player ID. Used to identify player, throws ResourceNotFoundException if not found.
     * @return Optional of Player object.
     * @throws ResourceNotFoundException Throws a ResourceNotFoundException if the player is not found.
     */
    @Override
    public Optional<Player> getPlayerById(final Integer playerId) {
        return playerRepository.findById(playerId);
    }

    /**
     * Updates the Players balance based on the transaction type.
     * @param playerId the player ID. Used to identify player, throws ResourceNotFoundException if not found.
     * @param requestedTransaction A UpdateRequestView Transaction that will contain the transaction amount and type
     *                              (Wager or Win).
     * @throws InsufficientFundsException Throws an InsufficientFundsException if the amount is greater than the current balance.
     * @throws ResourceNotFoundException Throws a ResourceNotFoundException if the player is not found.
     * @throws ConstraintViolationException Throws a ConstraintViolationException if the amount is negative.
     * @return A Transaction  object containing the transaction information
     */
    @Override
    @Transactional
    public Transaction updatePlayerBalance(final Integer playerId, @Valid final Transaction requestedTransaction) {
        final Optional<Player> player = playerRepository.findById(playerId);
        final Transaction transactionAudit = new Transaction();
        transactionAudit.setPlayerId(playerId);
        transactionAudit.setAmount(requestedTransaction.getAmount());
        transactionAudit.setTransactionType(requestedTransaction.getTransactionType());

        if (player.isPresent()) {
            final Player playerUpdate = player.get();

            if (TransactionType.isWin(requestedTransaction.getTransactionType())) {
                playerUpdate.setBalance(playerUpdate.getBalance() + requestedTransaction.getAmount());
            } else if (TransactionType.isWager(requestedTransaction.getTransactionType())) {
                if (playerUpdate.getBalance() < requestedTransaction.getAmount()) {
                    log.debug("Insufficient funds for player with id : {}", playerId);
                    // We could save the failed Transaction for auditing (With a Failed status)
                    throw new InsufficientFundsException("Insufficient funds for player with id : " + playerId);
                }
                playerUpdate.setBalance(playerUpdate.getBalance() - requestedTransaction.getAmount());
            }
            playerRepository.save(playerUpdate);
            transactionAudit.setRunningBalance(playerUpdate.getBalance());
            transactionRepository.save(transactionAudit);
            log.debug("Player Transaction successfully completed. Player ID : {} | username ; {}. " +
                            "Transaction ID : {} | Balance : {}", playerId, playerUpdate.getUsername(),
                    transactionAudit.getTransactionId(), transactionAudit.getRunningBalance());
        } else {
            log.debug("Player not found with id : {}", playerId);
            throw new ResourceNotFoundException("Player not found with id : " + playerId);
        }
        return transactionAudit;
    }

    /**
     * Gets the last ten wager/win transactions the player made on the slot game
     * @param username the username of the player. Used to identify player, throws ResourceNotFoundException if not found.
     * @throws ResourceNotFoundException Throws a ResourceNotFoundException if the player is not found.
     * @return List of Transaction objects containing the transactionId, transactionDateTime, transactionType,
     *         amount, runningBalance and username.
     *         If the player has no transactions, an empty list is returned.
     *         The list is sorted in descending order by transactionDateTime.
     *         The list is limited to the last 10 transactions.
     *         If the player has more than 10 transactions, the last 10 will be returned.
     *         If the player has less than 10 transactions, the list will contain all the transactions.
     */
    @Override
    public List<Transaction> lastTenTransactions(final String username) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent()) {
            final List<Transaction> transactions = transactionRepository.findByPlayerId(player.get().getPlayerId(),
                    PageRequest.of(0, 10,
                            Sort.by(Sort.Direction.DESC, "transactionDateTime"))).getContent();
            log.debug("Last ten transactions for player {}: {}", username, transactions);
            return transactions;
        } else {
            log.debug("Player not found with username : {}", username);
            throw new ResourceNotFoundException("Player not found with username : " + username);
        }
    }
}
