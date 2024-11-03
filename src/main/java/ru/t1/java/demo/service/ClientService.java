package ru.t1.java.demo.service;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.List;

public interface ClientService {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Client> registerClients(List<Client> clients);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Client registerClient(Client client);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account registerAccount(Account account);

    List<ClientDto> parseJson();

    void clearMiddleName(List<ClientDto> dtos);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account blockAccount(Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account unblockAccount(Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Transaction cancelTransaction(Long transactionId);
}
