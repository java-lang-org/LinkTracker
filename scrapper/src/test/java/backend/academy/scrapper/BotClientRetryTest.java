package backend.academy.scrapper;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

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
class BotClientRetryTest {
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
    public void shouldRetryOn5xxErrors() {
        // Arrange
        stubFor(post(urlEqualTo("/updates"))
                .inScenario("Retry 5xx")
                .whenScenarioStateIs("Started")
                .willReturn(serverError())
                .willSetStateTo("Second"));

        stubFor(post(urlEqualTo("/updates"))
                .inScenario("Retry 5xx")
                .whenScenarioStateIs("Second")
                .willReturn(ok()));

        // Act
        botClient.updates(new LinkUpdate(1L, "https://github.com/python/mypy", "description", List.of(10L, 20L)));

        // Assert
        verify(2, postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    public void shouldNotRetryOn4xxErrors() {
        // Arrange
        stubFor(post(urlEqualTo("/updates")).willReturn(aResponse().withStatus(400)));

        // Act
        try {
            botClient.updates(new LinkUpdate(1L, "https://github.com/python/mypy", "description", List.of(10L, 20L)));
        } catch (HttpServerErrorException e) {
            // empty
        }

        // Assert
        verify(1, postRequestedFor(urlEqualTo("/updates")));
    }
}
