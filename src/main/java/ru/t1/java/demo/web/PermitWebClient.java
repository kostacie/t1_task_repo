package ru.t1.java.demo.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import ru.t1.java.demo.model.dto.PermitRequest;
import ru.t1.java.demo.model.dto.PermitResponse;

import java.util.Optional;

@Slf4j
public class PermitWebClient extends BaseWebClient {

    @Value("${integration.resource-transaction}")
    private String resource;

    public PermitWebClient(WebClient webClient) {
        super(webClient);
    }

    public Optional<PermitResponse> permit(Long id) {
        log.debug("Старт запроса с id {}", id);
        ResponseEntity<PermitResponse> post = null;
        try {
            PermitRequest request = PermitRequest.builder()
                    .transactionId(id)
                    .build();

            post = this.post(
                    uriBuilder -> uriBuilder.path(resource).build(),
                    request,
                    PermitResponse.class);


        } catch (Exception httpStatusException) {
            throw httpStatusException;
        }

        log.debug("Финиш запроса с id {}", id);
        return Optional.ofNullable(post.getBody());
    }
}
