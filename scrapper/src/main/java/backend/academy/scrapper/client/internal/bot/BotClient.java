package backend.academy.scrapper.client.internal.bot;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class BotClient {
    private final RestClient restClient;
    private final BotConfig botConfig;

    public BotClient(@Qualifier("botRestClient") RestClient restClient, BotConfig botConfig) {
        this.restClient = restClient;
        this.botConfig = botConfig;
    }

    public void updates(LinkUpdate linkUpdate) {
        try {
            ResponseEntity<ApiErrorResponse> response = restClient
                    .post()
                    .uri(botConfig.url() + "/updates")
                    .body(linkUpdate)
                    .retrieve()
                    .toEntity(ApiErrorResponse.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Warn: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
