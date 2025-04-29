package backend.academy.bot;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkUpdate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(
        properties = "spring.config.name=application-test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BotControllerRateLimitingTest {
    @LocalServerPort
    private int port;

    @Value("${rate-limiting.max-requests-per-minute}")
    private int maxRequestsPerMinute;

    @Autowired
    private RestClient restClient;

    @Test
    void shouldReturnTooManyRequests_whenLimitExceeded() throws InterruptedException {
        // Arrange
        LinkUpdate linkUpdate =
                new LinkUpdate(1L, "https://github.com/python/cpython", "description", List.of(10L, 11L));

        // Act
        for (int i = 0; i < maxRequestsPerMinute; i++) {
            ResponseEntity<ApiErrorResponse> response =
                    restClient.post().uri(getUrl()).body(linkUpdate).retrieve().toEntity(ApiErrorResponse.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Assert
        ResponseEntity<ApiErrorResponse> response =
                restClient.post().uri(getUrl()).body(linkUpdate).retrieve().toEntity(ApiErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        Thread.sleep(60_000);

        response = restClient.post().uri(getUrl()).body(linkUpdate).retrieve().toEntity(ApiErrorResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String getUrl() {
        return "http://localhost:" + port + "/updates";
    }
}
