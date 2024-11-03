package ru.t1.java.demo.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.yml")
class TransactionServiceIntegrationTest {

    @MockBean
    TransactionRepository transactionRepository;

    @Autowired
    TransactionServiceImpl transactionService;


    @Test
    void permittedTest() {

        Transaction transaction1 = getTransaction();
        Transaction transaction2 = getTransaction();
        transaction2.setId(1634L);

        when(transactionRepository.save(transaction1)).thenReturn(transaction2);

        Transaction result = transactionService.permitted(transaction1);

        assertThat(result.getId()).isEqualTo(1634L);
    }

    private static Transaction getTransaction() {
        Transaction transaction = new Transaction();
        transaction.setClientId(55443L);
        transaction.setAccountId(76548L);
        transaction.setAmount(BigDecimal.valueOf(5555558));
        return transaction;
    }
}