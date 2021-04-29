package com.challenge.authorizer.model.entities;

public final class AccountEntity {

    private final AccountEntityImmutable accountEntityImmutable;

    public AccountEntity(AccountEntityImmutable accountEntityImmutable) {
        this.accountEntityImmutable = accountEntityImmutable;
    }

    public AccountEntityImmutable getAccount() {
        return accountEntityImmutable;
    }

}