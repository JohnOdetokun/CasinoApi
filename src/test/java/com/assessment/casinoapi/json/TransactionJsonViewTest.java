package com.assessment.casinoapi.json;

import com.assessment.casinoapi.model.Transaction;
import com.assessment.casinoapi.model.TransactionType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

public class TransactionJsonViewTest {
    private ObjectMapper mapper;

    private Transaction transaction;

    @BeforeEach
    public void setup() {
        mapper = JsonMapper.builder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();
        transaction = new Transaction(1222453L, 22344,
                550.55, 1000.25,
                TransactionType.WAGER, Instant.parse("2023-10-18T00:00:57.907Z"));
    }

    @Test
    public void UpdateRequestViewTest() throws Exception {
        final String result = mapper
                .writerWithView(Transaction.Views.UpdateRequestView.class)
                .writeValueAsString(transaction);

        assertThat(result, containsString(transaction.getTransactionType().toString()));
        assertThat(result, not(containsString(String.valueOf(transaction.getTransactionDateTime()))));
        assertThat(result, not(containsString(String.valueOf(transaction.getTransactionId()))));
        assertThat(result, not(containsString(String.valueOf(transaction.getRunningBalance()))));
        assertThat(result, containsString(String.valueOf(transaction.getAmount())));
    }

    @Test
    public void UpdateResponseViewTest() throws Exception {
        final String result = mapper
                .writerWithView(Transaction.Views.UpdateResponseView.class)
                .writeValueAsString(transaction);

        assertThat(result, not(containsString(transaction.getTransactionType().toString())));
        assertThat(result, not(containsString(String.valueOf(transaction.getTransactionDateTime()))));
        assertThat(result, containsString(String.valueOf(transaction.getTransactionId())));
        assertThat(result, containsString(String.valueOf(transaction.getRunningBalance())));
        assertThat(result, not(containsString(String.valueOf(transaction.getAmount()))));
    }
}
