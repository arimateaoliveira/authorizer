package com.challenge.authorizer.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AccountResponse {

    @JsonProperty("account")
    private final AccountResponseImmutable accountResponseImmutable;

    @JsonProperty("violations")
    private final List<String> violations;

    @Builder
    public AccountResponse(AccountResponseImmutable accountResponseImmutable, List<String> violations) {
        this.accountResponseImmutable = accountResponseImmutable;
        this.violations = violations;
    }

    @JsonProperty("account")
    public AccountResponseImmutable getAccount() {
        return accountResponseImmutable;
    }

}