package com.assessment.casinoapi.utils;

import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.respository.PlayerRepository;

public class PlayerTestHelper {
    public static Player getPlayer(final Integer playerId, final PlayerRepository playerRepository) {
        return playerRepository.findById(playerId).orElse(null);
    }

    public static Player createPlayer(final String username, final double initialBalance,
                                       final PlayerRepository playerRepository) {
        final Player  player = Player.builder()
                .username(username)
                .balance(initialBalance)
                .build();
        return playerRepository.save(player);
    }
}
