package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.FailedTransaction;

@Repository
public interface FailedTransactionRepository extends JpaRepository<FailedTransaction, Long> {
}
