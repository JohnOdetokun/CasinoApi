package com.assessment.casinoapi.model;

public enum TransactionType {
    WAGER,
    WIN;

    public static boolean isWager(final TransactionType transactionType) {
        return transactionType == WAGER;
    }

    public static boolean isWin(final TransactionType transactionType) {
        return transactionType == WIN;
    }
}
