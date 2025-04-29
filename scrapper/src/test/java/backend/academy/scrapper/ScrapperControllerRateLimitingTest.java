package backend.academy.scrapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import backend.academy.dto.ListLinksResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(
        properties = "spring.config.name=application-test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScrapperControllerRateLimitingTest {
    @LocalServerPort
    private int port;

    @Value("${rate-limiting.max-requests-per-minute}")
    private int maxRequestsPerMinute;

    @Autowired
    private RestClient botRestClient;

    @Test
    void shouldReturnTooManyRequests_whenLimitExceeded() throws InterruptedException {
        // Arrange
        long chatId = 10L;

        // Act
        for (int i = 0; i < maxRequestsPerMinute; i++) {
            ResponseEntity<ListLinksResponse> response = botRestClient
                    .get()
                    .uri(getUrl())
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .retrieve()
                    .toEntity(ListLinksResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        // Assert
        ResponseEntity<ListLinksResponse> response = botRestClient
                .get()
                .uri(getUrl())
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .retrieve()
                .toEntity(ListLinksResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        Thread.sleep(60_000);

        response = botRestClient
                .get()
                .uri(getUrl())
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .retrieve()
                .toEntity(ListLinksResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private String getUrl() {
        return "http://localhost:" + port + "/links";
    }

    private HttpHeaders headersWithChatId(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }
}
