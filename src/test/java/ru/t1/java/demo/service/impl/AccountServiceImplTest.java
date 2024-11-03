package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;


    @Test
    void saveTest() {
        Account account = getAccount();

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account result = accountService.save(account);

        assertEquals(account, result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void findById_foundTest() {
        Account account = getAccount();

        Long accountId = account.getId();
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Optional<Account> result = accountService.findById(accountId);

        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findById_notFoundTest() {
        Account account = getAccount();
        Long accountId = account.getId();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        Optional<Account> result = accountService.findById(accountId);

        assertTrue(result.isEmpty());
        verify(accountRepository, times(1)).findById(accountId);
    }

    private static Account getAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setClientId(10034L);
        account.setAccountType(AccountType.DEBIT);
        return account;
    }
}