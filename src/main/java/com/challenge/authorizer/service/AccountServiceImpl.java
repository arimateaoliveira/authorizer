package com.challenge.authorizer.service;

import com.challenge.authorizer.model.entities.AccountEntity;
import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import com.challenge.authorizer.model.request.AccountRequest;
import com.challenge.authorizer.model.response.AccountResponseImmutable;
import com.challenge.authorizer.repository.AccountRepository;
import com.challenge.authorizer.util.Constants;
import com.challenge.authorizer.util.AccountConverters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AccountServiceImpl implements AccountService{

    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountConverters accountConverters;

    @Override
    public String save(AccountRequest accountRequest) {

        LOG.info("Validate account begin : " + accountRequest.toString());

        List<String> violationsList = new ArrayList<String>();

        violationsRules(violationsList);

        String accountResponse = "";
        if(violationsList.size() == 0) {
            AccountEntity accountEntity = AccountConverters.getAccountImmutable(accountRequest);
            accountRepository.save(accountEntity.getAccount());
            accountResponse = getAccountOutput(violationsList, accountEntity.getAccount());
        } else {
            AccountEntityImmutable accountEntityImmutable = accountRepository.findAll().get(0);
            accountResponse = getAccountOutput(violationsList, accountEntityImmutable);
            LOG.info("ERROR: " + Constants.ACCOUNT_NOT_INITIALIZED);
        }

        LOG.info("Validate account end: " + accountResponse);
        return accountResponse;

    }

    public void violationsRules(List<String> violationsList){

        String accountAlreadyInitialized = validateAccountAlreadyInitialized();
        if(!accountAlreadyInitialized.isEmpty()){
            violationsList.add(accountAlreadyInitialized);
        }

    }

    @Override
    public Integer getAccountLimit(){

        List<AccountEntityImmutable> accountEntityImmutableList = accountRepository.findAll();

        Integer limit = 0;
        if(accountEntityImmutableList.size() > 0){
            limit = accountEntityImmutableList.get(0).getAvailablelimit();
        }

        return limit - transactionService.sumTransactionsAmounts();

    }

    @Override
    public String validateCardNotActive(){

        List<AccountEntityImmutable> accountEntityImmutableList = accountRepository.findAll();
        String message = "";
        Boolean isCardActive = false;
        if(accountEntityImmutableList.size() > 0){
            isCardActive = accountEntityImmutableList.get(0).getActivecard();
        }

        if (!isCardActive){
            message = Constants.CARD_NOT_ACTIVE;
        }

        return message;
    }

    public String validateAccountNotInitialized(){

        List<AccountEntityImmutable> accountEntityImmutableList = accountRepository.findAll();

        String message = "";
        if (accountEntityImmutableList.size() == 0) {
            message = Constants.ACCOUNT_NOT_INITIALIZED;
        }

        return message;

    }

    public String validateAccountAlreadyInitialized(){
        List<AccountEntityImmutable> accountEntityImmutableList = accountRepository.findAll();

        String accountAlreadyInitialized = "";
        if (accountEntityImmutableList.size() > 0) {
            accountAlreadyInitialized = Constants.ACCOUNT_ALREADY_INITIALIZED;
        }

        return accountAlreadyInitialized;
    }

    public String getAccountOutput(List<String> violationsList, AccountEntityImmutable accountEntityImmutable) {

        AccountResponseImmutable accountResponseImmutable = accountConverters.converterAccountEntityToResponse(
                accountEntityImmutable.getActivecard(), accountEntityImmutable.getAvailablelimit(), violationsList);

        String accountOutPut = AccountConverters.getJsonFromAccount(accountResponseImmutable, violationsList);

        return accountOutPut;

    }

}
