package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

//@RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repository;
    @Override
    public void save(Transaction transaction) {
        repository.save(transaction);
    }

    public void saveAll(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }
}
