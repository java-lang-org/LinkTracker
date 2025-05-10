package backend.academy.scrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertTrue;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.client.internal.bot.BotClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpServerErrorException;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(properties = "spring.config.name=application-test")
public class BotClientCircuitBreakerTest {
    private static WireMockServer wireMockServer;

    @Autowired
    private BotClient botClient;

    @DynamicPropertySource
    static void overrideBotUrl(DynamicPropertyRegistry registry) {
        registry.add("bot.url", wireMockServer::baseUrl);
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
        LinkUpdate linkUpdate =
                new LinkUpdate(1L, "https://github.com/python/cpython", "description", List.of(10L, 20L));

        wireMockServer.stubFor(get(urlEqualTo("/updates"))
                .willReturn(aResponse().withFixedDelay(5000).withStatus(200)));

        // Act
        for (int i = 0; i < 5; i++) {
            updates(linkUpdate);
        }

        long startMillis = System.currentTimeMillis();
        updates(linkUpdate);
        long endMillis = System.currentTimeMillis();
        long durationMillis = endMillis - startMillis;

        // Assert
        assertTrue(durationMillis < 100);
    }

    private void updates(LinkUpdate linkUpdate) {
        try {
            botClient.updates(linkUpdate);
        } catch (HttpServerErrorException e) {
            // empty
        }
    }
}
