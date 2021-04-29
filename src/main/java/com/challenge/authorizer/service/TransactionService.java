package com.challenge.authorizer.service;

import com.challenge.authorizer.model.entities.TransactionEntityImmutable;
import com.challenge.authorizer.model.request.TransactionRequest;

public interface TransactionService {

    String authorize(TransactionRequest transactionRequest);
    TransactionEntityImmutable save(TransactionEntityImmutable transaction);
    Integer sumTransactionsAmounts();

}
