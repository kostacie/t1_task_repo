package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.PermitResponse;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.web.CheckWebClient;
import ru.t1.java.demo.web.PermitWebClient;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final PermitWebClient permitWebClient;

    @Override
    public void save(Transaction transaction) {
        repository.save(transaction);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }

    @Override
    @Transactional
    public Transaction permitted(Transaction transaction) {
        Optional<PermitResponse> response = permitWebClient.permit(transaction.getId());
        Transaction saved = null;
        if (response.isPresent()) {
            if (response.get().getPermitted())
                saved = repository.save(transaction);
        }
        return saved;
    }
}
