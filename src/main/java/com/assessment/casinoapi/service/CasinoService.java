package com.assessment.casinoapi.service;

import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface CasinoService {
    Optional<Player> getPlayerById(Integer playerId);

    Transaction updatePlayerBalance(Integer playerId, Transaction transaction);

    List<Transaction> lastTenTransactions(String username);
}
