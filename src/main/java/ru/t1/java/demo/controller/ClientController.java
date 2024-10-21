package ru.t1.java.demo.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.TrackExecutionTime;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    private final KafkaClientProducer kafkaClientProducer;
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Value("${t1.kafka.topic.client_registration}")
    private String topic;

    @HandlingResult
    @GetMapping(value = "/parse")
    public void parseSource() throws Exception {
            clientRepository.save(Client.builder()
                    .firstName("John42")
                    .build());
            clientRepository.findClientByFirstName("John42");
            throw new ClientException("ClientException has been raised.");
    }

    @GetMapping(value = "/client")
    @TrackExecutionTime
    public void doSomething() throws IOException, InterruptedException {
        Thread.sleep(5000L);
        clientRepository.findClientByFirstName("John42");
    }

    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
