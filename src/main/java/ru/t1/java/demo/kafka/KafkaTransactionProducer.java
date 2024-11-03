package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, TransactionDto> template;

    @Value("${t1.kafka.topic.client_transactions}")
    private String topic;

    public void send(TransactionDto transaction) {
        try {
            template.send(topic, transaction);
            log.info("Successfully sent transaction: {}", transaction);
        } catch (Exception ex) {
            log.error("Error sending transaction: {}", ex.getMessage());
        } finally {
            template.flush();
        }
    }
}

