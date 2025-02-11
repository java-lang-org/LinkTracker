package backend.academy.scrapper;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkUpdate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class BotClient {
    private final RestClient restClient;

    @Value("${bot.url}")
    private String scrapperUrl;

    public BotClient(@Qualifier("botRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void updates(Link link, String description, List<Long> chatIds) {
        try {
            ResponseEntity<ApiErrorResponse> response = restClient.post()
                .uri(scrapperUrl + "/updates")
                .body(new LinkUpdate(link.hashCode(), link.url(), description, chatIds))
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
