package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account save(Account account);
    void saveAll(List<Account> accounts);
    Optional<Account> findById(Long id);
}
