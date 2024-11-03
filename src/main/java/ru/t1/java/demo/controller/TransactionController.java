package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;


    @GetMapping("/info")
    @Transactional
    @PreAuthorize("hasRole('USER')")
    // Сервис получает по http-запросу информацию о транзакции по счету клиента (списание, начисление)
    public ResponseEntity<Transaction> getDebitOrCreditTransaction(@RequestBody TransactionDto dto) {
        log.info("Received request for account: {}", dto.getAccountId());
        Transaction transaction = TransactionMapper.toEntity(dto);
        Account account = accountRepository.findById(dto.getAccountId()).orElse(null);
        if (account != null) {
            log.info("Transaction: id={}, clientId={}, accountId={}, amount={}, accountType={}",
                    transaction.getId(), transaction.getClientId(), transaction.getAccountId(),
                    transaction.getAmount(), account.getAccountType());

            return ResponseEntity.ok().body(transaction);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("info/cancel/{transactionId}")
    @Transactional
    @PreAuthorize("hasRole('USER')")
//    Сервис получает по http-запросу информацию о транзакции по счету клиента (отмена)
    public ResponseEntity<Transaction> getCancelTransaction(@PathVariable Long transactionId) {
        log.info("Received cancellation request for transaction id: {}", transactionId);
        Transaction canceled = transactionRepository.findById(transactionId).orElse(null);
        if (canceled != null) {
            Account account = accountRepository.findById(canceled.getAccountId()).orElse(null);
            log.info("Transaction canceled: id={}, clientId={}, accountId={}, amount={}, accountType={}",
                    transactionId, canceled.getClientId(), canceled.getAccountId(), canceled.getAmount(), account.getAccountType());
            return ResponseEntity.ok().body(canceled);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/permit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transaction> permit(@RequestBody TransactionDto transactionDto) {
        log.info("Checking transaction: {}", transactionDto);
        Transaction allowed = transactionService.permitted(
                TransactionMapper.toEntity(transactionDto)
        );
        return ResponseEntity.ok().body(allowed);
    }
}
