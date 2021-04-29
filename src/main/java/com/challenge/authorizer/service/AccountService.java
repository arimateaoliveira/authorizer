package com.challenge.authorizer.service;

import com.challenge.authorizer.model.request.AccountRequest;

public interface AccountService {

    String save(AccountRequest accountRequest);
    Integer getAccountLimit();
    String validateCardNotActive();
    String validateAccountNotInitialized();

}
