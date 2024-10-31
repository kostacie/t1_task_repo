package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    @Transactional
    public Account save(Account account) {
        return repository.save(account);
    }

    @Override
    @Transactional
    public void saveAll(List<Account> accounts) {
        repository.saveAll(accounts);
    }

    @Override
    @Transactional
    public Optional<Account> findById(Long id) {
        return repository.findById(id);
    }
}
