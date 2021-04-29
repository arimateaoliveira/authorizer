package com.challenge.authorizer.controller;

import com.challenge.authorizer.model.request.AccountRequest;
import com.challenge.authorizer.model.request.TransactionRequest;
import com.challenge.authorizer.service.AccountService;
import com.challenge.authorizer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(AccountController.ACCOUNT)
public class AccountController {

    static final String ACCOUNT = "/account";

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveAccount(@RequestBody AccountRequest request, UriComponentsBuilder builder) {

        String result = accountService.save(request);

        return ResponseEntity.ok().body(result);

    }

}
