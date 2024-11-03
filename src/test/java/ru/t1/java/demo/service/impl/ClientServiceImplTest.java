package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.exception.NotYetImplementedException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

//    @Spy
//    ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    @Spy
    ClientServiceImpl clientService;

    @Mock
    ClientServiceImpl clientServiceMock;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Test
    void parseJsonSpy() {
        when(clientService.parseJson()).thenReturn(List.of(ClientDto.builder()
                .build()));

        assertEquals(List.of(ClientDto.builder().build()), clientService.parseJson());
    }

    @Test
    void parseJsonMock() {

        when(clientServiceMock.parseJson()).thenReturn(List.of(ClientDto.builder().build()));

        assertEquals(List.of(ClientDto.builder().build()), clientServiceMock.parseJson());
    }

    @Test
    void blockAccountSuccessTest() {
        Long accountId = 1L;
        Account account = getAccount();
        account.setBlocked(false);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        var result = clientService.unblockAccount(accountId);

        assertEquals(account, result);
    }

    @Test
    void unblockAccountSuccessTest() {
        Long accountId = 1L;
        Account account = getAccount();
        account.setBlocked(true);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        var result = clientService.unblockAccount(accountId);

        assertEquals(account, result);
    }

    @Test
    void cancelTransactionSuccessTest() {
        Long transactionId = 154L;
        Long accountId = 2875L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setAmount(new BigDecimal("100.00"));

        Account account = getAccount();

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(accountRepository.findById(transaction.getAccountId())).thenReturn(Optional.of(account));

        var result = clientService.cancelTransaction(transactionId);

        assertNotNull(result);
    }

    private static Account getAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        account.setClientId(1334L);
        account.setAccountType(AccountType.DEBIT);
        return account;
    }
}