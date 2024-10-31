package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.mapper.TransactionMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaClientConsumer {

    private final ClientService clientService;
    private final TransactionService transactionService;
    private final AccountService accountService;

    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.client_registration}",
            containerFactory = "clientKafkaListenerContainerFactory")
    public void clientListener(@Payload List<ClientDto> messageList,
                               Acknowledgment ack,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Client consumer: Обработка новых сообщений");

        try {
            List<Client> clients = messageList.stream()
                    .map(dto -> {
                        dto.setFirstName(key + "@" + dto.getFirstName());
                        return ClientMapper.toEntity(dto);
                    })
                    .toList();
            clientService.registerClients(clients);
        } finally {
            ack.acknowledge();
        }


        log.debug("Client consumer: записи обработаны");
    }

    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.client_accounts}",
            containerFactory = "accountKafkaListenerContainerFactory")
    public void accountListener(@Payload List<AccountDto> messageList,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка новых сообщений");

        try {
            List<Account> accounts = messageList.stream()
                    .map(AccountMapper::toEntity)
                    .toList();
            accountService.saveAll(accounts);
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: записи обработаны");
    }
}
