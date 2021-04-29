package com.challenge.authorizer.service;

import com.challenge.authorizer.model.entities.AccountEntityImmutable;
import com.challenge.authorizer.model.request.AccountRequest;
import com.challenge.authorizer.model.request.AccountRequestImmutable;
import com.challenge.authorizer.model.response.AccountResponseImmutable;
import com.challenge.authorizer.repository.AccountRepository;
import com.challenge.authorizer.util.Constants;
import com.challenge.authorizer.util.AccountConverters;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountConverters accountConverters;

    @Test
    public void testSaveAccountWhenAllAreNew() {

        AccountRequest accountRequest = getAccountRequest();

        AccountEntityImmutable accountEntityImmutableExpected = getAccountExpected();
        AccountResponseImmutable accountResponseImmutable = getAccountResponseImmutable();

        when(accountRepository.save(any(AccountEntityImmutable.class))).thenReturn(accountEntityImmutableExpected);
        when(accountConverters.converterAccountEntityToResponse(any(), any(), any())).thenReturn(accountResponseImmutable);

        String response = accountService.save(accountRequest);

        verify(accountRepository, times(1)).save(any(AccountEntityImmutable.class));
        assertNotNull(response);

    }

    @Test
    public void testSaveAccountWhenAccountAlreadyInitialized() {

        AccountRequest accountRequest = getAccountRequest();
        List<AccountEntityImmutable> accountEntityImmutableList = getAccountEntityImmutableList();

        AccountEntityImmutable accountEntityImmutableExpected = getAccountExpected();
        AccountResponseImmutable accountResponseImmutable = getAccountResponseImmutableWhitAccountAlreadyInicialized();

        when(accountRepository.save(any(AccountEntityImmutable.class))).thenReturn(accountEntityImmutableExpected);
        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);
        when(accountConverters.converterAccountEntityToResponse(any(), any(), any())).thenReturn(accountResponseImmutable);

        String response = accountService.save(accountRequest);

        verify(accountRepository, times(0)).save(any(AccountEntityImmutable.class));
        assertNotNull(response);
        assertTrue(response.contains(Constants.ACCOUNT_ALREADY_INITIALIZED));

    }

    @Test
    public void testValidateAccountInitialized(){

        List<AccountEntityImmutable> accountEntityImmutableList = getAccountFieldsList();
        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);

        String accountInicialized = accountService.validateAccountNotInitialized();

        verify(accountRepository, times(1)).findAll();
        assertTrue(accountInicialized.isEmpty());

    }

    @Test
    public void testValidateCardNotActive() {

        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<>();
        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);

        String response = accountService.validateCardNotActive();

        verify(accountRepository, times(1)).findAll();
        assertNotNull(response);
        assertTrue(response.equals(Constants.CARD_NOT_ACTIVE));

    }

    @Test
    public void testValidateCardActive() {

        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<>();
        accountEntityImmutableList.add(accountEntityImmutable);

        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);

        String response = accountService.validateCardNotActive();

        verify(accountRepository, times(1)).findAll();
        assertNotNull(response);
        assertTrue(response.isEmpty());

    }

    @Test
    public void testAccountLimit() {

        List<AccountEntityImmutable> accountEntityImmutableList = getAccountEntityImmutableList();
        AccountEntityImmutable accountEntityImmutable = getAccountFields();
        accountEntityImmutableList.add(accountEntityImmutable);

        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);
        when(transactionService.sumTransactionsAmounts()).thenReturn(10);

        Integer accountLimit = accountService.getAccountLimit();

        verify(accountRepository, times(1)).findAll();
        assertNotNull(accountLimit);
        assertTrue(accountLimit.equals(90));

    }


    @Test
    public void testValidateAccountAlreadyInitialized() {

        List<AccountEntityImmutable> accountEntityImmutableList = getAccountEntityImmutableList();

        when(accountRepository.findAll()).thenReturn(accountEntityImmutableList);

        String response = accountService.validateAccountAlreadyInitialized();

        verify(accountRepository, times(1)).findAll();
        assertNotNull(response);
        assertTrue(response.contains(Constants.ACCOUNT_ALREADY_INITIALIZED));

    }

    private AccountResponseImmutable getAccountResponseImmutable() {
        final List<String> violations = new ArrayList<>();
        return new AccountResponseImmutable(true, 100);
    }

    private AccountResponseImmutable getAccountResponseImmutableWhitAccountAlreadyInicialized() {
        final List<String> violations = new ArrayList<>();
        violations.add(Constants.ACCOUNT_ALREADY_INITIALIZED);
        return new AccountResponseImmutable(true, 100);
    }

    private List<AccountEntityImmutable> getAccountEntityImmutableList() {

        List<AccountEntityImmutable> accountEntityImmutablesList = new ArrayList<>();
        AccountEntityImmutable accountEntityImmutable = getAccountExpected();
        accountEntityImmutablesList.add(accountEntityImmutable);

        return accountEntityImmutablesList;
    }

    private List<AccountEntityImmutable> getAccountFieldsList() {

        List<AccountEntityImmutable> accountEntityImmutableList = new ArrayList<AccountEntityImmutable>();

        AccountEntityImmutable accountEntityImmutable1 = getAccountFields();
        AccountEntityImmutable accountEntityImmutable2 = getAccountFields();

        accountEntityImmutableList.add(accountEntityImmutable1);
        accountEntityImmutableList.add(accountEntityImmutable2);

        return accountEntityImmutableList;

    }

    private AccountEntityImmutable getAccountFields() {
        return new AccountEntityImmutable(1, true, 100);
    }

    private AccountEntityImmutable getAccountExpected() {
        return new AccountEntityImmutable(1, true, 100);
    }

    private AccountRequest getAccountRequest() {
        AccountRequestImmutable accountRequestImmutable = new AccountRequestImmutable(
                true, 100);

        AccountRequest accountRequest = new AccountRequest(accountRequestImmutable);

        return accountRequest;
    }


}
