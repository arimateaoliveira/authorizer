package com.challenge.authorizer.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequest {

    @JsonProperty("transaction")
    private TransactionRequestImmutable transactionRequestImmutable;

    public TransactionRequest() {

    }

    public TransactionRequest(TransactionRequestImmutable transactionRequestImmutable) {
        this.transactionRequestImmutable = transactionRequestImmutable;
    }

    @JsonProperty("transaction")
    public TransactionRequestImmutable getTransactionRequestImmutable() {
        return transactionRequestImmutable;
    }

}