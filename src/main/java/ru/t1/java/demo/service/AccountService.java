package ru.t1.java.demo.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account save(Account account);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void saveAll(List<Account> accounts);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findById(Long id);
}
