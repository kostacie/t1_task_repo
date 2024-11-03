package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.FailedTransaction;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.FailedTransactionRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.CorrectionService;
import ru.t1.java.demo.web.BaseWebClient;


@Service
@RequiredArgsConstructor
@Slf4j
public class CorrectionServiceImpl implements CorrectionService {

    private final WebClient webClient;
    private final TransactionRepository transactionRepository;
    private final FailedTransactionRepository failedTransactionRepository;
    private final KafkaTransactionProducer transactionProducer;

    // При получении сообщения отправляет в сервис 1 http-запрос на разблокировку счета.
    // При успешном ответе удалить запись о транзакции в БД, если таковая существует.
    // При получении отказа на разблокировку, создать запись в БД о такой транзакции,
    // если таковой еще не существует.
    @Transactional
    @KafkaListener(topics = "${t1.kafka.topic.client_transaction_errors}",
            groupId = "${t1.kafka.consumer.group-id}",
            containerFactory = "failedTransactionKafkaListenerContainerFactory")
    public void listenTransactionErrors(@Payload Long transactionId, Acknowledgment ack) {
        try {

            Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

            if (transaction != null) {
                var response = sendRequest(transaction);

                if (response != null && response.getStatusCode() == HttpStatus.OK) {
                    // При успешной разблокировке удаляем запись если она есть
                    failedTransactionRepository.deleteById(transactionId);
                    log.info("Transaction record deleted: {}", transactionId);
                } else {
                    // При неуспешной разблокировке создаем запись если её нет
                    failedTransactionRepository.findById(transactionId)
                            .orElseGet(() -> {
                                FailedTransaction newTransaction = FailedTransaction.builder()
                                        .clientId(transaction.getClientId())
                                        .accountId(transaction.getAccountId())
                                        .amount(transaction.getAmount())
                                        .build();
                                failedTransactionRepository.save(newTransaction);
                                log.info("Created new failed transaction record: {}", transactionId);
                                return newTransaction;
                            });
                }
            }
        } finally {
            ack.acknowledge();
        }
    }

    @Scheduled(fixedDelayString = "${track.fixed-delay}")
    @Transactional
    public void retryFailedTransactions() {
        for (FailedTransaction failedTransaction : failedTransactionRepository.findAll()) {
            try {
                Transaction transaction = buildTransaction(failedTransaction);
                var response = sendRequest(transaction);
                if (response != null && failedTransaction.getId() != null && response.getStatusCode() == HttpStatus.OK) {
                    transactionRepository.deleteById(failedTransaction.getId());
                    transactionProducer.send(TransactionMapper.toDto(transaction));
                }
            } catch (Exception e) {
                log.error("Unexpected error occurred while sending the request: {}", e.getMessage());
            }
        }
    }

    private ResponseEntity<String> sendRequest(Transaction transaction) {
        return webClient.post()
                .uri("/accounts/{accountId}/unblock", transaction.getAccountId())
                .bodyValue(TransactionMapper.toDto(transaction))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    private Transaction buildTransaction(FailedTransaction failedTransaction) {
        return Transaction.builder()
                .clientId(failedTransaction.getClientId())
                .accountId(failedTransaction.getAccountId())
                .amount(failedTransaction.getAmount())
                .build();
    }
}

