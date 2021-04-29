package com.challenge.authorizer.model.entities;

public final class TransactionEntity {

    private final TransactionEntityImmutable transactionEntityImmutable;

    public TransactionEntity (TransactionEntityImmutable transactionEntityImmutable) {
        this.transactionEntityImmutable = transactionEntityImmutable;
    }

    public TransactionEntityImmutable getTransactionEntityImmutable() {
        return transactionEntityImmutable;
    }

}