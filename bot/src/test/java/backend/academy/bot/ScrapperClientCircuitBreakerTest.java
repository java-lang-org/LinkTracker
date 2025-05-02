package backend.academy.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(properties = "spring.config.name=application-test")
public class ScrapperClientCircuitBreakerTest {
    private static WireMockServer wireMockServer;

    @Autowired
    private ScrapperClient scrapperClient;

    @DynamicPropertySource
    static void overrideScrapperUrl(DynamicPropertyRegistry registry) {
        registry.add("scrapper.url", wireMockServer::baseUrl);
    }

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    public static void clear() {
        wireMockServer.stop();
    }

    @AfterEach
    public void reset() {
        WireMock.reset();
    }

    @Test
    public void circuitBreakerShouldOpenAfterFailures() {
        // Arrange
        long chatId = 1L;

        wireMockServer.stubFor(get(urlEqualTo("/links"))
                .willReturn(aResponse().withFixedDelay(5000).withStatus(200)));

        // Act
        for (int i = 0; i < 5; i++) {
            scrapperClient.getLinks(chatId);
        }

        long startMillis = System.currentTimeMillis();
        scrapperClient.getLinks(chatId);
        long endMillis = System.currentTimeMillis();
        long durationMillis = endMillis - startMillis;

        // Assert
        assertTrue(durationMillis < 100);
    }
}
