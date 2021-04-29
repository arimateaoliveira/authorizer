package com.challenge.authorizer.util;

import com.challenge.authorizer.model.entities.AccountEntity;
import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import com.challenge.authorizer.model.entities.TransactionEntity;
import com.challenge.authorizer.model.entities.TransactionEntityImmutable;
import com.challenge.authorizer.model.request.AccountRequest;
import com.challenge.authorizer.model.request.TransactionRequest;
import com.challenge.authorizer.model.response.AccountResponse;
import com.challenge.authorizer.model.response.AccountResponseImmutable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountConverters {

    public AccountResponseImmutable converterAccountEntityToResponse(Boolean activeCard,
                                                                     Integer limitUpdated, List<String> violations){

        AccountResponseImmutable accountResponseImmutable = new AccountResponseImmutable(
                activeCard, limitUpdated);

        return accountResponseImmutable;

    }

    public static AccountEntity getAccountImmutable(AccountRequest accountRequest) {
        AccountEntity accountEntity = new AccountEntity(
                new AccountEntityImmutable(0L,
                        accountRequest.getAccount().getActivecard(),
                        accountRequest.getAccount().getAvailablelimit()));

        return accountEntity;
    }

    public static TransactionEntity getTransactionImmutable(TransactionRequest transactionRequest) {
        TransactionEntity TransactionEntity = new TransactionEntity(
                new TransactionEntityImmutable(0L,
                        transactionRequest.getTransactionRequestImmutable().getMerchant(),
                        transactionRequest.getTransactionRequestImmutable().getAmount(),
                        transactionRequest.getTransactionRequestImmutable().getTime()));

        return TransactionEntity;
    }

    public static AccountRequest getAccountFromJson(String accountJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        AccountRequest accountRequest = null;

        try {
            accountRequest = objectMapper.readValue(accountJson, AccountRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return accountRequest;
    }

    public static String getJsonFromAccount(AccountResponseImmutable accountResponseImmutable,  List<String> violations) {

        AccountResponse accountResponse = new AccountResponse(accountResponseImmutable, violations);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAccount = "";

        try {
            jsonAccount = objectMapper.writeValueAsString(accountResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonAccount;
    }

}
