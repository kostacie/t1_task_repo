package ru.t1.java.demo.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionService {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void save(Transaction transaction);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void saveAll(List<Transaction> transactions);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Transaction permitted(Transaction transaction);
}
