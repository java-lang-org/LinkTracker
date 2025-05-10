package backend.academy.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.core.JsonProcessingException;
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
class ScrapperClientRetryTest {
    private static WireMockServer wireMockServer;

    @Autowired
    private ScrapperClient scrapperClient;

    @DynamicPropertySource
    static void overrideBotUrl(DynamicPropertyRegistry registry) {
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
    public void shouldRetryOn5xxErrors() {
        // Arrange
        stubFor(get(urlEqualTo("/links"))
                .inScenario("Retry 5xx")
                .whenScenarioStateIs("Started")
                .willReturn(serverError())
                .willSetStateTo("Second"));

        stubFor(get(urlEqualTo("/links"))
                .inScenario("Retry 5xx")
                .whenScenarioStateIs("Second")
                .willReturn(ok()));

        // Act
        scrapperClient.getLinks(1L);

        // Assert
        verify(2, getRequestedFor(urlEqualTo("/links")));
    }

    @Test
    public void shouldNotRetryOn4xxErrors() throws JsonProcessingException {
        // Arrange
        stubFor(get(urlEqualTo("/links")).willReturn(aResponse().withStatus(400)));

        // Act
        scrapperClient.getLinks(1L);

        // Assert
        verify(1, getRequestedFor(urlEqualTo("/links")));
    }
}
