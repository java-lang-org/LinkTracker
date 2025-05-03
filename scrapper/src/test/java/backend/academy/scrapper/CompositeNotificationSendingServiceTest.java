package backend.academy.scrapper;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.service.impl.CompositeNotificationSendingService;
import backend.academy.scrapper.service.impl.HttpNotificationSendingService;
import backend.academy.scrapper.service.impl.KafkaNotificationSendingService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.HttpServerErrorException;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(properties = "spring.config.name=application-test")
public class CompositeNotificationSendingServiceTest {
    @MockitoSpyBean
    private ScrapperConfig scrapperConfig;

    @MockitoSpyBean
    private HttpNotificationSendingService httpNotificationSendingService;

    @MockitoSpyBean
    private KafkaNotificationSendingService kafkaNotificationSendingService;

    @MockitoSpyBean
    private CompositeNotificationSendingService compositeNotificationSendingService;

    @Test
    void shouldFallbackToKafkaOnHttpFailure() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://github.com/python/mypy", "description", List.of(10L, 20L));

        when(scrapperConfig.messageTransport()).thenReturn(ScrapperConfig.MessageTransport.HTTP);

        doThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
                .when(httpNotificationSendingService)
                .sendNotification(linkUpdate);

        // Act
        compositeNotificationSendingService.sendNotification(linkUpdate);

        // Assert
        verify(httpNotificationSendingService, times(1)).sendNotification(linkUpdate);
        verify(kafkaNotificationSendingService, times(1)).sendNotification(linkUpdate);
    }

    @Test
    void shouldFallbackToHttpOnKafkaFailure() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(1L, "https://github.com/python/mypy", "description", List.of(10L, 20L));

        when(scrapperConfig.messageTransport()).thenReturn(ScrapperConfig.MessageTransport.Kafka);

        doThrow(new RuntimeException("Kafka error"))
                .when(kafkaNotificationSendingService)
                .sendNotification(linkUpdate);

        // Act
        compositeNotificationSendingService.sendNotification(linkUpdate);

        // Assert
        verify(httpNotificationSendingService, times(1)).sendNotification(linkUpdate);
        verify(kafkaNotificationSendingService, times(1)).sendNotification(linkUpdate);
    }
}
