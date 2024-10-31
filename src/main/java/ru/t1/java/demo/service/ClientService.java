package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<Client> registerClients(List<Client> clients);

    Client registerClient(Client client);

    Account registerAccount(Account account);

    List<ClientDto> parseJson();

    void clearMiddleName(List<ClientDto> dtos);

    Account blockAccount(Long accountId);

    Account unblockAccount(Long accountId);

    Transaction cancelTransaction(Long transactionId);
}
