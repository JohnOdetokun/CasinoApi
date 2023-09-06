package com.assessment.casinoapi.json;

import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class TransactionJsonTest {
    @Autowired
    private JacksonTester<Transaction> json;

    private Transaction transaction;

    @BeforeEach
    public void setup() {
        transaction = new Transaction(1222453L, 22344,
                550.55, 1000.25,
                TransactionType.WAGER, Instant.parse("2023-10-18T00:00:57.907Z"));
    }

    @Test
    public void transactionSerializeTest() throws Exception {
        assertThat(json.write(transaction)).isEqualToJson("transaction_example.json");
    }

    @Test
    public void playerBalanceDeserializeTest() throws Exception {
        final String expected = """
                {
                   "transactionId": 1222453,
                   "amount": 550.55,
                   "transactionType": "WAGER",
                   "transactionDateTime": "2023-10-18T00:00:57.907Z"
                 }
                """;
        assertThat(json.parseObject(expected).getTransactionId()).isEqualTo(1222453);
        assertThat(json.parseObject(expected).getAmount()).isEqualTo(550.55);
        assertThat(json.parseObject(expected).getTransactionType()).isEqualTo(TransactionType.WAGER);
        assertThat(json.parseObject(expected).getTransactionDateTime()).isEqualTo(Instant.parse("2023-10-18T00:00:57.907Z"));
    }
}
