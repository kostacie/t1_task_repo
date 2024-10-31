package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountType;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    @Value("${t1.kafka.topic.client_transaction_errors}")
    private String transactionErrors;

    // Сервис слушает топик t1_demo_client_transactions, создает новую запись о транзакции, изменяет состояние баланса
    @Transactional
    @KafkaListener(groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.client_transactions}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void transactionListener(@Payload TransactionDto transactionDto, Acknowledgment ack) {
        try {
            Transaction transaction = TransactionMapper.toEntity(transactionDto);
            Account account = accountService.findById(transaction.getAccountId()).orElse(null);
            var saved = transactionRepository.save(transaction);
            if (account != null && !account.isBlocked()) {
                BigDecimal balance = account.getBalance();
                BigDecimal amount = transaction.getAmount();

                if (AccountType.CREDIT.equals(account.getAccountType())) {
                    account.setBalance(balance.subtract(amount));
                } else if (AccountType.DEBIT.equals(account.getAccountType())) {
                    account.setBalance(balance.add(amount));
                }
                accountService.save(account);
                // если счет заблокирован, отправить id ошибочной транзакции в топик
            } else {
                kafkaTemplate.send(transactionErrors, saved.getId());
                log.error("Account is blocked. Transaction id {} sent to topic 'transaction_errors'", transaction.getId());
            }
        } finally {
            ack.acknowledge();
        }
    }
}
