package com.assessment.casinoapi.utils;

import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import com.assessment.casinoapi.respository.TransactionRepository;

public class TransactionTestHelper {
    public static Transaction createTransaction(final Player player, final TransactionType transactionType,
                                                final double amount, final double runningBalance,
                                                final TransactionRepository transactionRepository) {
        final Transaction transaction = Transaction.builder()
                .playerId(player.getPlayerId())
                .transactionType(transactionType)
                .amount(amount)
                .runningBalance(runningBalance)
                .build();
        return transactionRepository.save(transaction);
    }
}
