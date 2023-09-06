package com.assessment.casinoapi.controller;

import com.assessment.casinoapi.exception.InsufficientFundsException;
import com.assessment.casinoapi.exception.ResourceNotFoundException;
import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.service.CasinoService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.*;
import static org.springframework.http.ResponseEntity.badRequest;


/**
 * This class is a controller for the casino API.
 */

@Slf4j
@RestController
@RequestMapping("/casino")
public class CasinoController {
    private final CasinoService casinoService;

    public CasinoController(CasinoService casinoService) {
        this.casinoService = casinoService;
    }

    @GetMapping("/player/{playerId}/balance")
    @JsonView(Player.Views.BalanceView.class)
    public ResponseEntity<Player> getPlayerBalance(@PathVariable("playerId") Integer playerId) {
        final Optional<Player> player = casinoService.getPlayerById(playerId);
        return player.map(value -> ok().body(value)).orElseGet(() -> badRequest().build());
    }

    @PostMapping("/player/{playerId}/balance/update")
    @JsonView(Transaction.Views.UpdateResponseView.class)
    public ResponseEntity<Transaction> updatePlayerBalance(
            @PathVariable("playerId") Integer playerId,
            @RequestBody @JsonView(Transaction.Views.UpdateRequestView.class) Transaction transaction) {
        try {
            return ok().body(casinoService.updatePlayerBalance(playerId, transaction));
        } catch (final ResourceNotFoundException | ConstraintViolationException e) {
            log.debug("Invalid player {}", playerId);
            return badRequest().build();
        } catch (final InsufficientFundsException e) {
            log.debug("Insufficient funds for player {}", playerId);
            return status(HttpStatus.I_AM_A_TEAPOT).build();
        } catch (final Exception e) {
            log.error("Unexpected error. Please contact support.", e);
            throw e;
        }
    }

    @PostMapping("/admin/player/transactions")
    @JsonView(Transaction.Views.HistoryView.class)
    public ResponseEntity<Transaction[]> lastTenTransactions(
            @Valid @RequestBody @JsonView(Player.Views.HistoryView.class) Player player) {
        try {
            final List<Transaction> transactions = casinoService.lastTenTransactions(player.getUsername());
            log.debug("Last ten transactions for player {}: {}", player.getUsername(), transactions);
            return ok().body(transactions.toArray(new Transaction[0]));
        } catch (final ResourceNotFoundException e) {
            log.debug("Player not found with username : {}", player.getUsername());
            return badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error. Please contact support, {}", e.getMessage());
            return badRequest().build();
        }
    }
}
