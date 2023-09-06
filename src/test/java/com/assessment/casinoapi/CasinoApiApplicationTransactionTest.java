package com.assessment.casinoapi;

import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import com.assessment.casinoapi.respository.PlayerRepository;
import com.assessment.casinoapi.respository.TransactionRepository;
import com.assessment.casinoapi.utils.PlayerTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CasinoApiApplicationTransactionTest {
    @Autowired
    TestRestTemplate restTemplate;

    @SpyBean
    TransactionRepository transactionRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Test
    void updateBalance_transactionPersistFails() {
        Player player = PlayerTestHelper.createPlayer("TransactionUser", 500.0, playerRepository);

        doThrow(new RuntimeException("Error saving transaction"))
                .when(transactionRepository)
                .save(any());

        final Transaction transaction = Transaction.builder()
                .amount(100.0)
                .transactionType(TransactionType.WAGER)
                .build();
        ResponseEntity<Transaction> updateResponse = restTemplate
                .postForEntity("/casino/player/12345/balance/update", transaction, Transaction.class);

        // Balance should remain the same
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        Player updatedPlayer = PlayerTestHelper.getPlayer(player.getPlayerId(), playerRepository);
        assertThat(player).isEqualTo(updatedPlayer);

        // No new transactions should be persisted
        ResponseEntity<Transaction[]> listTransactionsResponse = restTemplate
                .postForEntity("/casino/admin/player/transactions", player, Transaction[].class);
        assertThat(listTransactionsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(listTransactionsResponse.getBody()).length).isEqualTo(0);
    }

}
