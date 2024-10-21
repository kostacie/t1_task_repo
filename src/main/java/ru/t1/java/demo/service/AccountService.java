package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface AccountService {
    void save(Account account);
    void saveAll(List<Account> accounts);
}
