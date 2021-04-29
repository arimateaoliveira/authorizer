package com.challenge.authorizer.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountRequest {

    @JsonProperty("account")
    private AccountRequestImmutable accountRequestImmutable;

    @JsonProperty("account")
    public AccountRequestImmutable getAccount() {
        return accountRequestImmutable;
    }

    public AccountRequest(){};

    public AccountRequest(AccountRequestImmutable accountRequestImmutable){
        this.accountRequestImmutable = accountRequestImmutable;
    };

}