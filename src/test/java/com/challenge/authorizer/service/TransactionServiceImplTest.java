package com.challenge.authorizer.service;

import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import com.challenge.authorizer.model.entities.TransactionEntityImmutable;
import com.challenge.authorizer.model.request.TransactionRequest;
import com.challenge.authorizer.model.request.TransactionRequestImmutable;
import com.challenge.authorizer.model.response.AccountResponseImmutable;
import com.challenge.authorizer.repository.AccountRepository;
import com.challenge.authorizer.repository.TransactionRepository;
import com.challenge.authorizer.util.Constants;
import com.challenge.authorizer.util.AccountConverters;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountConverters accountConverters;

    @Test
    public void testAuthorizeWhenTransactionIsNew() {

        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        AccountResponseImmutable accountResponse = getAccountResponse();
        List<AccountEntityImmutable> accountEntityImmutableList = getAccountFieldsList();
        String validateCardNotActive = "";
        String validateAccountNotInitialized = "";

        when(accountService.validateAccountNotInitialized()).thenReturn(validateAccountNotInitialized);
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntityImmutable));
        when(accountService.validateCardNotActive()).thenReturn(validateCardNotActive);

        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);
        when(accountConverters.converterAccountEntityToResponse(any(), any(), any())).thenReturn(accountResponse);

        TransactionRequest transactionRequest = getTransactionRequest();
        String response = transactionService.authorize(transactionRequest);

        verify(transactionRepository, times(1)).findAll();
        verify(transactionRepository, times(1)).save(any());

        assertNotNull(response);
        assertTrue(isJSONValid(response));

    }

    @Test
    public void testAuthorizeWhenAccountNotInitialized() {

        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        AccountResponseImmutable accountResponse = getAccountResponse();
        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<>();
        String validateCardNotActive = "";
        String validateAccountNotInitialized = "account-not-initialized";

        String transactionJson = getNewTransaction();

        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntityImmutable));
        when(accountService.validateAccountNotInitialized()).thenReturn(validateAccountNotInitialized);
        when(accountService.validateCardNotActive()).thenReturn(validateCardNotActive);
        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);
        when(accountConverters.converterAccountEntityToResponse(any(), any(), any())).thenReturn(accountResponse);

        TransactionRequest transactionRequest = getTransactionRequest();
        String response = transactionService.authorize(transactionRequest);

        verify(transactionRepository, times(1)).findAll();
        verify(transactionRepository, times(0)).save(any());

        assertNotNull(response);
        assertTrue(isJSONValid(response));
        assertTrue(response.contains(Constants.ACCOUNT_NOT_INITIALIZED));

    }

    @Test
    public void testDoubleTransaction() {

        TransactionRequest transactionToSave = getTransactionRequest();

        TransactionEntityImmutable transactionExpected = getTransactionEntityImmutable();
        List<TransactionEntityImmutable> transactionEntityImmutableListExpected = new ArrayList<>();
        transactionEntityImmutableListExpected.add(transactionExpected);

        when(transactionRepository.findTopByMerchantAndAmountOrderByTimeDesc(anyString(), any(Integer.class)))
                .thenReturn(transactionEntityImmutableListExpected);

        String response = transactionService.validateDoubledTransaction(transactionToSave);

        verify(transactionRepository, times(1)).findTopByMerchantAndAmountOrderByTimeDesc(anyString(), any(Integer.class));

        assertNotNull(response);
        assertTrue(response.contains(Constants.DOUBLED_TRANSACTION));

    }

    @Test
    public void testWhenHighFrequencySmallInterval() {

        TransactionRequest transactionToSave = getTransactionRequest();

        List<TransactionEntityImmutable> transactionEntityImmutableListExpected = getThreeTransactionFieldsList();

        when(transactionRepository.findAllByTimeAfter(any()))
                .thenReturn(transactionEntityImmutableListExpected);

        String response = transactionService.validateHighFrequencySmallInterval(transactionToSave);

        verify(transactionRepository, times(1)).findAllByTimeAfter(any());
        assertTrue(response.contains(Constants.HIGH_FREQUENCY_SMALL_INTERVAL));

    }

    @Test
    public void testWhenLimitIsSuficient() {

        AccountEntityImmutable accountEntityImmutable = getAccountFields();

        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntityImmutable));

        String response = transactionService.validateLimit(110);

        verify(accountRepository, times(1)).findById(any());
        assertTrue(response.contains(Constants.INSUFFICIENT_LIMIT));

    }


    @Test
    public void testWhenWhenAllRulesFail() {

        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        AccountResponseImmutable accountResponse = getAccountResponse();
        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<>();
        String validateCardNotActive = "card-not-active";
        String validateAccountNotInitialized = "account-not-initialized";

        String transactionJson = getNewTransaction();

        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntityImmutable));
        when(accountService.validateCardNotActive()).thenReturn(validateCardNotActive);
        when(accountService.validateAccountNotInitialized()).thenReturn(validateAccountNotInitialized);
        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);
        when(accountConverters.converterAccountEntityToResponse(any(), any(), any())).thenReturn(accountResponse);

        TransactionRequest transactionRequest = getTransactionRequest();
        String response = transactionService.authorize(transactionRequest);

        verify(transactionRepository, times(1)).findAll();
        verify(transactionRepository, times(0)).save(any());

        assertNotNull(response);
        assertTrue(isJSONValid(response));

        System.out.println(response);

    }

    @Test
    public void testSumTransactionsAmounts() {

        List<TransactionEntityImmutable> transactionEntityImmutableList = getTransactionFieldsList();

        when(transactionRepository.findAll()).thenReturn(transactionEntityImmutableList);

        Integer sumAmounts = transactionService.sumTransactionsAmounts();

        verify(transactionRepository, times(1)).findAll();

        assertNotNull(sumAmounts);
        assertEquals(30, sumAmounts);

    }

    @Test
    public void testSaveTransactionWhenAllAreNew() {

        TransactionEntityImmutable expectedTransactionToSave = getTransactionEntityImmutable();
        TransactionEntityImmutable transactionEntityImmutable = getTransactionEntityImmutable();

        when(transactionRepository.save(any(TransactionEntityImmutable.class))).thenReturn(expectedTransactionToSave);

        TransactionEntityImmutable transactionSaved = transactionService.save(transactionEntityImmutable);

        verify(transactionRepository, times(1)).save(any(TransactionEntityImmutable.class));
        assertNotNull(transactionSaved);

    }

    @Test
    public void testSimilarTransaction() {

        TransactionRequest transactionRequest = getTransactionRequest();
        TransactionEntityImmutable transactionEntityImmutableBd = getTransactionEntityImmutable();
        Boolean isSimilarTransaction = transactionService.getSimilarTransaction(transactionRequest,
                transactionEntityImmutableBd,false);

        assertTrue(isSimilarTransaction);
    }

    @Test
    public void testGetResponseError() {

        List<String> violations = new ArrayList<>();
        violations.add(Constants.ACCOUNT_NOT_INITIALIZED);

        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        when(accountRepository.findById(any())).thenReturn(Optional.of(accountEntityImmutable));

        String response = transactionService.getResponseError(violations);

        assertTrue(!response.isEmpty());

    }

    private TransactionEntityImmutable getTransactionEntityImmutable(){

        TransactionEntityImmutable transactionEntityImmutable = new TransactionEntityImmutable();
        Timestamp time = Timestamp.valueOf("2020-02-19 22:55:03.9");

        transactionEntityImmutable.setId(1L);
        transactionEntityImmutable.setAmount(10);
        transactionEntityImmutable.setMerchant("Burger King");
        transactionEntityImmutable.setTime(time);


        return transactionEntityImmutable;
    }

    private TransactionRequest getTransactionRequest(){

        Timestamp time = Timestamp.valueOf("2020-02-19 22:56:03.9");

        TransactionRequestImmutable transactionEntityImmutable = new TransactionRequestImmutable(
                "Burger King",10, time);

        TransactionRequest transactionRequest = new TransactionRequest(transactionEntityImmutable);

        return transactionRequest;
    }

    private List<TransactionEntityImmutable> getTransactionFieldsList(){

        List<TransactionEntityImmutable> transactionEntityImmutableList = new ArrayList<TransactionEntityImmutable>();

        TransactionEntityImmutable transactionEntityImmutable1 = getTransactionEntityImmutable();
        TransactionEntityImmutable transactionEntityImmutable2 = getTransactionEntityImmutable();
        TransactionEntityImmutable transactionEntityImmutable3 = getTransactionEntityImmutable();

        transactionEntityImmutableList.add(transactionEntityImmutable1);
        transactionEntityImmutableList.add(transactionEntityImmutable2);
        transactionEntityImmutableList.add(transactionEntityImmutable3);

        return transactionEntityImmutableList;

    }

    private List<TransactionEntityImmutable> getThreeTransactionFieldsList(){

        List<TransactionEntityImmutable> transactionEntityImmutableList = new ArrayList<TransactionEntityImmutable>();

        TransactionEntityImmutable transactionEntityImmutableOne = getTransactionEntityImmutable();
        Timestamp time = Timestamp.valueOf("2020-02-19 22:57:03.9");
        transactionEntityImmutableOne.setTime(time);

        TransactionEntityImmutable transactionEntityImmutableTwo = getTransactionEntityImmutable();
        TransactionEntityImmutable transactionEntityImmutableThree = getTransactionEntityImmutable();


        transactionEntityImmutableList.add(transactionEntityImmutableOne);
        transactionEntityImmutableList.add(transactionEntityImmutableTwo);
        transactionEntityImmutableList.add(transactionEntityImmutableThree);

        return transactionEntityImmutableList;

    }

    private String getNewTransaction(){
        return "{\"transaction\": {\"merchant\": \"Burger King\", \"amount\": 20, \"time\":\n" +
                "\"2019-02-13T10:00:00.000Z\"}}";
    }

    private AccountEntityImmutable getAccountFields() {
        return new AccountEntityImmutable(1, true, 100);
    }

    private List<AccountEntityImmutable> getAccountFieldsList() {

        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<AccountEntityImmutable>();

        AccountEntityImmutable accountEntityImmutable1 = getAccountFields();
        AccountEntityImmutable accountEntityImmutable2 = getAccountFields();

        accountEntityImmutableList.add(accountEntityImmutable1);
        accountEntityImmutableList.add(accountEntityImmutable2);

        return accountEntityImmutableList;

    }

    private AccountResponseImmutable getAccountResponse(){

        List<String> violations = new ArrayList<>();
        violations.add(Constants.ACCOUNT_NOT_INITIALIZED);

        AccountResponseImmutable accountResponseImmutable = new AccountResponseImmutable(
                true, 100);

        return accountResponseImmutable;

    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}