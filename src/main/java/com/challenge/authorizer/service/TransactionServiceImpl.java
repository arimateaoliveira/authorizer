package com.challenge.authorizer.service;

import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import com.challenge.authorizer.model.entities.TransactionEntity;
import com.challenge.authorizer.model.entities.TransactionEntityImmutable;
import com.challenge.authorizer.model.request.TransactionRequest;
import com.challenge.authorizer.model.response.AccountResponseImmutable;
import com.challenge.authorizer.repository.AccountRepository;
import com.challenge.authorizer.repository.TransactionRepository;
import com.challenge.authorizer.util.Constants;
import com.challenge.authorizer.util.AccountConverters;
import com.challenge.authorizer.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

@Service
public class TransactionServiceImpl implements TransactionService{

    private static final Logger LOG = Logger.getLogger(AccountServiceImpl.class.getName());

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountConverters accountConverters;

    @Override
    public String authorize(TransactionRequest transactionRequest) {

        LOG.info("Authorization transaction begin : " + transactionRequest.toString());

        List<String> violationsList = new ArrayList<String>();
        String transactionResponse = "";

        validateRules(transactionRequest, violationsList);

        if(violationsList.size() == 0) {
            Optional<AccountEntityImmutable> optional = accountRepository.findById(1L);
            TransactionEntity transactionEntityImmutable = AccountConverters.getTransactionImmutable(transactionRequest);
            save(transactionEntityImmutable.getTransactionEntityImmutable());
            transactionResponse = getAccountOutput(violationsList, optional.get());
        } else {
            transactionResponse = getResponseError(violationsList);
        }

        LOG.info("Authorization transaction end: " + transactionResponse);

        return transactionResponse;

    }

    public String getResponseError(List<String> violationsList) {
        String transactionResponse;
        Optional<AccountEntityImmutable> optional = accountRepository.findById(1L);
        if(!optional.isPresent()){
            AccountEntityImmutable accountEntityImmutable = new AccountEntityImmutable(0, false, 0);
            transactionResponse = getAccountOutput(violationsList, accountEntityImmutable);
        } else {
            transactionResponse = getAccountOutput(violationsList, optional.get());
        }
        LOG.info("ERROR: " + violationsList.toString());
        return transactionResponse;
    }

    public void validateRules(TransactionRequest transactionRequest, List<String> violationsList) {

        String accountNotInitialized = accountService.validateAccountNotInitialized();
        if (!accountNotInitialized.isEmpty()) {
            violationsList.add(accountNotInitialized);
        }

        String cardNotActive = accountService.validateCardNotActive();
        if(!cardNotActive.isEmpty()){
            violationsList.add(cardNotActive);
        }

        String limit = validateLimit(transactionRequest.getTransactionRequestImmutable().getAmount());
        if (!limit.isEmpty()){
            violationsList.add(limit);
        }

        String highFrequencySmallInterval = validateHighFrequencySmallInterval(transactionRequest);
        if (!highFrequencySmallInterval.isEmpty()) {
            violationsList.add(highFrequencySmallInterval);
        }

        String doubledTransaction = validateDoubledTransaction(transactionRequest);
        if (!doubledTransaction.isEmpty()) {
            violationsList.add(doubledTransaction);
        }

    }

    @Override
    public TransactionEntityImmutable save(TransactionEntityImmutable transactionEntity) {
        LOG.info("Saving transaction begin");
        TransactionEntityImmutable transactionBd = transactionRepository.save(transactionEntity);
        LOG.info("Saving transaction end ");
        return transactionBd;
    }

    public String getAccountOutput(List<String> violationsList, AccountEntityImmutable accountEntityImmutable) {

        Integer limitUpdated = accountService.getAccountLimit();

        AccountResponseImmutable accountResponseImmutable = accountConverters.converterAccountEntityToResponse(
                accountEntityImmutable.getActivecard(), limitUpdated, violationsList);

        String accountOutPut = AccountConverters.getJsonFromAccount(accountResponseImmutable, violationsList);

        return accountOutPut;

    }

    public Integer sumTransactionsAmounts(){
        List<TransactionEntityImmutable> transactionEntityImmutableList = transactionRepository.findAll();

        Integer sum = transactionEntityImmutableList.stream()
                .map(x -> x.getAmount())
                .reduce(0, Integer::sum);

        return sum;
    }

    public String validateLimit(Integer amount){

        String limitMessage = "";

        Optional<AccountEntityImmutable> optional = accountRepository.findById(1L);

        Integer limit = 0;
        if(optional.isPresent()){
            limit = optional.get().getAvailablelimit();
        }

        if((sumTransactionsAmounts() + amount) > limit){
            limitMessage= Constants.INSUFFICIENT_LIMIT;
        }

        return limitMessage;

    }

    public String validateHighFrequencySmallInterval(TransactionRequest transactionRequest){

        String highFrequencySmallInterval = "";

        LocalDateTime timeCurrentTransaction = DateUtils.getLocalDateTime(
                transactionRequest.getTransactionRequestImmutable().getTime());

        timeCurrentTransaction = timeCurrentTransaction.minusSeconds(120);
        Date startDate = DateUtils.convertToDateViaSqlTimestamp(timeCurrentTransaction);
        List<TransactionEntityImmutable> transactionEntityImmutableList =
                transactionRepository.findAllByTimeAfter(startDate);

        highFrequencySmallInterval = calculateDiffTimeInterval(transactionRequest, highFrequencySmallInterval, transactionEntityImmutableList);

        return highFrequencySmallInterval;

    }

    private String calculateDiffTimeInterval(TransactionRequest transactionRequest, String highFrequencySmallInterval, List<TransactionEntityImmutable> transactionEntityImmutableList) {
        long diffTime = 0;
        int transactions = 0;

        if (transactionEntityImmutableList.size() >= 3 ) {

            transactions = transactionEntityImmutableList.size() + 1;

            TransactionEntityImmutable transactionEntityImmutableBd = transactionEntityImmutableList.get(0);
            LocalDateTime timeFirstTransaction = DateUtils.getLocalDateTime(transactionEntityImmutableBd.getTime());
            LocalDateTime timeThirdTransaction = DateUtils.getLocalDateTime(
                    transactionRequest.getTransactionRequestImmutable().getTime());

            diffTime = ChronoUnit.SECONDS.between(timeFirstTransaction, timeThirdTransaction);

        }

        if (diffTime <= 120 && transactions >= 4){
            highFrequencySmallInterval = Constants.HIGH_FREQUENCY_SMALL_INTERVAL;
        }
        return highFrequencySmallInterval;
    }


    public String validateDoubledTransaction(TransactionRequest transactionRequest){

        String doubledTransaction = "";

        List<TransactionEntityImmutable> transactionEntityImmutableList =
                transactionRepository.findTopByMerchantAndAmountOrderByTimeDesc(
                        transactionRequest.getTransactionRequestImmutable().getMerchant(),
                        transactionRequest.getTransactionRequestImmutable().getAmount());

        TransactionEntityImmutable transactionEntityImmutableBd = null;
        long diffTime = 0;
        Boolean isSimilarTransaction = false;

        if (transactionEntityImmutableList.size() > 0 ) {
            transactionEntityImmutableBd = transactionEntityImmutableList.get(0);

            LocalDateTime timeFirstTransaction = DateUtils.convertDateToLocalDateTime(transactionEntityImmutableBd.getTime());
            LocalDateTime timeThirdTransaction = DateUtils.convertDateToLocalDateTime(
                    transactionRequest.getTransactionRequestImmutable().getTime());

            diffTime = ChronoUnit.SECONDS.between(timeFirstTransaction, timeThirdTransaction);

            isSimilarTransaction = getSimilarTransaction(transactionRequest, transactionEntityImmutableBd, isSimilarTransaction);

        }
        if (diffTime < 120 && isSimilarTransaction){
            doubledTransaction = Constants.DOUBLED_TRANSACTION;
        }

        return doubledTransaction;
    }

    public Boolean getSimilarTransaction(TransactionRequest transactionRequest, TransactionEntityImmutable transactionEntityImmutableBd, Boolean isSimilarTransaction) {
        if (transactionEntityImmutableBd.getMerchant().equals(transactionRequest.getTransactionRequestImmutable().getMerchant())
            && transactionEntityImmutableBd.getAmount().equals(transactionRequest.getTransactionRequestImmutable().getAmount())){
            isSimilarTransaction = true;
        }
        return isSimilarTransaction;
    }

}
