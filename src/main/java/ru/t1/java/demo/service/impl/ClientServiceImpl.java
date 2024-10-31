package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.t1.java.demo.exception.NotYetImplementedException;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.web.CheckWebClient;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final KafkaClientProducer kafkaClientProducer;
    private final CheckWebClient checkWebClient;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = checkWebClient.check(client.getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
                    Client saved = clientRepository.save(client);
                    kafkaClientProducer.send(saved.getId());
                    savedClients.add(saved);
                }
            });
//            savedClients.add(repository.save(client));
        }

        return savedClients;
    }

    @Override
    @Transactional
    public Client registerClient(Client client) {
        Client saved = null;
        Optional<CheckResponse> check = checkWebClient.check(client.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = clientRepository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }
        return saved;
    }

    @Transactional
    public Account registerAccount(Account account) {
        Account saved = null;
        Optional<CheckResponse> check = checkWebClient.check(account.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = accountRepository.save(account);
            }
        }
        return saved;
    }

    @Transactional
    public List<Account> registerAccounts(List<Account> accounts) {
        List<Account> savedAccounts = new ArrayList<>();
        for (Account account : accounts) {
            Optional<CheckResponse> check = checkWebClient.check(account.getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
                    Account saved = accountRepository.save(account);
                    savedAccounts.add(saved);
                }
            });
        }

        return savedAccounts;
    }

    @Transactional
    public Account blockAccount(Long accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotYetImplementedException("Account not found: " + accountId));
            account.setBlocked(true);
            return accountRepository.save(account);
        } catch (NotYetImplementedException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Transactional
    public Account unblockAccount(Long accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotYetImplementedException("Account not found: " + accountId));
            account.setBlocked(false);
            return accountRepository.save(account);
        } catch (NotYetImplementedException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Transactional
    public Transaction cancelTransaction(Long transactionId) {
        var optTrans = transactionRepository.findById(transactionId);
        if (optTrans.isPresent()) {
            Transaction transaction = optTrans.get();
            var optAcc = accountRepository.findById(transaction.getAccountId());
            if (optAcc.isPresent()) {
                Account account = optAcc.get();
                account.setBalance(account.getBalance().add(transaction.getAmount()));
                transactionRepository.deleteById(transactionId);
                return transaction;
            }
        }
        return null;
    }

    @Override
    public List<ClientDto> parseJson() {
        log.info("Parsing json");
        ObjectMapper mapper = new ObjectMapper();
        ClientDto[] clients = new ClientDto[0];
        try {
            clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            log.warn("Exception: ", e);
        }
        log.info("Found {} clients", clients.length);
        return Arrays.asList(clients);
    }

    @Override
    public void clearMiddleName(List<ClientDto> dtos) {
        log.info("Clearing middle name");
        dtos.forEach(dto -> dto.setMiddleName(null));
        log.info("Done clearing middle name");
    }
}
