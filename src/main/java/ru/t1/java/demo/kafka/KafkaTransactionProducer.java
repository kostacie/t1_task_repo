package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, Transaction> template;

    @Value("${t1.kafka.topic.client_transactions}")
    private String topic;

    public void send(Transaction transaction) {
        try {
            template.send(topic, String.valueOf(transaction.getAccountId()), transaction);
            log.info("Successfully sent transaction: {}", transaction.getId());
        } catch (Exception ex) {
            log.error("Error sending transaction: {}", ex.getMessage());
        } finally {
            template.flush();
        }
    }
}

