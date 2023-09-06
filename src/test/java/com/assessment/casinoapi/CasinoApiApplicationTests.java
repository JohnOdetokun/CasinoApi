package com.assessment.casinoapi;

import com.assessment.casinoapi.model.Player;
import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import com.assessment.casinoapi.respository.PlayerRepository;
import com.assessment.casinoapi.respository.TransactionRepository;
import com.assessment.casinoapi.utils.PlayerTestHelper;
import com.assessment.casinoapi.utils.TransactionTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CasinoApiApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    PlayerRepository playerRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void getBalance_existingUser() {
        final ResponseEntity<Player> response = restTemplate.getForEntity("/casino/player/1234/balance", Player.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getBalance()).isEqualTo(550.50);
        assertThat(Objects.requireNonNull(response.getBody()).getUsername()).isNull();
    }

    @Test
    void getBalance_nonExistingUser() {
        final ResponseEntity<Player> response = restTemplate.getForEntity("/casino/player/1111/balance", Player.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateBalance_nonExistingUser() {
        final Transaction transaction = Transaction.builder()
                .amount(100.0)
                .transactionType(TransactionType.WAGER)
                .build();
        ResponseEntity<Transaction> response = restTemplate
                .postForEntity("/casino/player/9999/balance/update", transaction, Transaction.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateBalance_wagerWithSufficientFunds() {
        final Transaction transaction = Transaction.builder()
                .amount(100.0)
                .transactionType(TransactionType.WAGER)
                .build();
        ResponseEntity<Transaction> response = restTemplate
                .postForEntity("/casino/player/12345/balance/update", transaction, Transaction.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTransactionId()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getRunningBalance()).isEqualTo(1450.50);
    }

    @Test
    void updateBalance_wagerWithInsufficientFunds() {
        final Transaction transaction = Transaction.builder()
                .amount(500.0)
                .transactionType(TransactionType.WAGER)
                .build();
        ResponseEntity<Transaction> response = restTemplate
                .postForEntity("/casino/player/123456/balance/update", transaction, Transaction.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT);
    }

    @Test
    void updateBalance_win() {
        final Transaction transaction = Transaction.builder()
                .amount(100.0)
                .transactionType(TransactionType.WIN)
                .build();
        ResponseEntity<Transaction> response = restTemplate
                .postForEntity("/casino/player/1234567/balance/update", transaction, Transaction.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getTransactionId()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getRunningBalance()).isEqualTo(450650.50);
    }

    @Test
    void updateBalance_negativeAmount() {
        final Transaction transaction = Transaction.builder()
                .amount(-100.0)
                .transactionType(TransactionType.WIN)
                .build();
        ResponseEntity<Transaction> response = restTemplate
                .postForEntity("/casino/player/1234567/balance/update", transaction, Transaction.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void lastTenTransactions_usernameGreaterThanMaxLength() {
        final Player player = Player.builder().username("A".repeat(55)).build();
        ResponseEntity<Object> response = restTemplate
                .postForEntity("/casino/admin/player/transactions", player, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void lastTenTransactions_existingUser_noTransactions() {
        final Player player = PlayerTestHelper.createPlayer("TheBeginner", 0, playerRepository);
        ResponseEntity<Transaction[]> response = restTemplate
                .postForEntity("/casino/admin/player/transactions", player, Transaction[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isEqualTo(0);
    }

    @Test
    void lastTenTransactions_existingUser() {
        final Player player = PlayerTestHelper.createPlayer("TheIntermediate", 500, playerRepository);
        final int transactionCount = 2;
        createTransactions(player, transactionCount);
        ResponseEntity<Transaction[]> response = restTemplate
                .postForEntity("/casino/admin/player/transactions", player, Transaction[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isEqualTo(transactionCount);
        Arrays.stream(response.getBody()).forEach(transaction -> {
            assertThat(transaction.getTransactionId()).isNotNull();
            assertThat(transaction.getAmount()).isGreaterThan(0);
            assertThat(transaction.getTransactionType()).isNotNull();
        });
    }

    @Test
    void lastTenTransactions_greaterThanTen() {
        final Player player = PlayerTestHelper.createPlayer("RegularPlayer", 5000.0, playerRepository);
        final int transactionCount = 15;
        createTransactions(player, transactionCount);
        ResponseEntity<Transaction[]> response = restTemplate
                .postForEntity("/casino/admin/player/transactions", player, Transaction[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).length).isEqualTo(10);
        Arrays.stream(response.getBody()).forEach(transaction -> {
            assertThat(transaction.getTransactionId()).isNotNull();
            assertThat(transaction.getAmount()).isGreaterThan(0);
            assertThat(transaction.getTransactionType()).isNotNull();
        });
    }

    private List<Transaction> createTransactions(final Player player, final int count) {
        final List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TransactionType transactionType = randomTransactionType();
            final double amount = 100;
            final double runningBalance = transactionType == TransactionType.WAGER ? player.getBalance() - amount : player.getBalance() + amount;
            final Transaction transaction = TransactionTestHelper.createTransaction(player, transactionType, amount,
                    runningBalance, transactionRepository);
            transactions.add(transaction);
        }
        return transactions;
    }

    public static TransactionType randomTransactionType() {
        final List<TransactionType> values = List.of(TransactionType.values());
        final int size = values.size();
        final Random RANDOM = new Random();
        return values.get(RANDOM.nextInt(size));
    }
}
