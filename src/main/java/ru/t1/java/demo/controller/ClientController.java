package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LoggableException;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.enums.Metrics;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.MetricService;
import ru.t1.java.demo.util.ClientMapper;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    private final AccountService accountService;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final MetricService metricService;
    private final KafkaTransactionProducer transactionProducer;
    private final WebClient webClient;


    @HandlingResult
    @GetMapping(value = "/parse")
    @LoggableException
    public void parseSource() {
        clientRepository.save(Client.builder()
                .firstName("John42")
                .build());
        clientRepository.findClientByFirstName("John42");
        metricService.incrementByName(Metrics.CLIENT_CONTROLLER_REQUEST_COUNT.getValue());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Client> register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        Client client = clientService.registerClient(
                clientMapper.toEntityWithId(clientDto)
        );
//        log.info("Client registered: {}", client.getId());
        return ResponseEntity.ok().body(client);
    }

    @PostMapping("/{clientId}/accounts/register")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Account> registerAccount(@PathVariable Long clientId, @RequestBody AccountDto accountDto) {
        log.info("Registering account: {}", accountDto);
        Account account = clientService.registerAccount(AccountMapper.toEntity(accountDto));
//        account.setClientId(clientId);
        return ResponseEntity.ok().body(account);
    }

    @PostMapping("/accounts/{accountId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> block(@PathVariable Long accountId) {
        Account account = clientService.blockAccount(accountId);
        if (account != null && account.isBlocked()) {
            return ResponseEntity.ok("Account blocked.");
        }
        return ResponseEntity.badRequest().body("Account not found or already blocked.");
    }

    @Transactional
    @PostMapping("/accounts/{accountId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unblock(@PathVariable Long accountId, @RequestBody TransactionDto transaction) {
        Account account = accountService.findById(accountId).orElse(null);
        if (account != null) {
            AccountType accountType = account.getAccountType();
            BigDecimal balance = account.getBalance();
            // Если счет депозитный, то разблокировать счет
            if (AccountType.DEBIT.equals(accountType)) {
                clientService.unblockAccount(accountId);
                return ResponseEntity.ok("Account unblocked successfully");
            }
            // Если счет кредитный и к моменту получения запроса на нем достаточно средств,
            // то разблокировать счет и обработать транзакцию повторно
            if (AccountType.CREDIT.equals(accountType) && balance.compareTo(BigDecimal.ZERO) > 0) {
                clientService.unblockAccount(accountId);
                transactionProducer.send(transaction);
            } else // Если средств недостаточно, то в ответном сообщении ответить отказом на запрос разблокировки
                return ResponseEntity.badRequest().body("Refused to unblock account.");

            return ResponseEntity.ok("Account unblocked successfully");
        }
        return ResponseEntity.badRequest().body("Account not found or already unblocked.");
    }

    // В случае, если поступило сообщение об "отмене" транзакции, удалить транзакцию из БД с корректировкой баланса.
    @PostMapping("/cancel/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> cancel(@PathVariable Long transactionId) {
        var canceled = clientService.cancelTransaction(transactionId);
        if (canceled != null) {
            sendRequest(canceled);
            return ResponseEntity.ok("Transaction cancelled successfully.");
        } else
            return ResponseEntity.badRequest().body("Transaction can't be cancelled.");
    }

    private void sendRequest(Transaction transaction) {
        webClient.get()
                .uri("/info/cancel/{transactionId}", transaction.getId())
                .retrieve()
                .toEntity(String.class)
                .block();
    }


}
