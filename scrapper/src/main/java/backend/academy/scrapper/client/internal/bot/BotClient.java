package backend.academy.scrapper.client.internal.bot;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.config.BotConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class BotClient {
    private final RetryTemplate retryTemplate;
    private final RestClient restClient;
    private final BotConfig botConfig;

    public BotClient(
            RetryTemplate retryTemplate, @Qualifier("botRestClient") RestClient restClient, BotConfig botConfig) {
        this.retryTemplate = retryTemplate;
        this.restClient = restClient;
        this.botConfig = botConfig;
    }

    @CircuitBreaker(name = "bot-client", fallbackMethod = "updatesFallback")
    public void updates(LinkUpdate linkUpdate) {
        try {
            ResponseEntity<ApiErrorResponse> response = retryTemplate.execute(context -> restClient
                    .post()
                    .uri(botConfig.url() + "/updates")
                    .body(linkUpdate)
                    .retrieve()
                    .toEntity(ApiErrorResponse.class));
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Warn: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private List<String> updatesFallback(LinkUpdate linkUpdate, Throwable throwable) {
        log.warn("Warning while executing \"updates for bot client\" for link {}", linkUpdate.url(), throwable);
        return List.of();
    }
}
