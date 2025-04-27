package backend.academy.bot;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import backend.academy.dto.LinkUpdate;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Import({TestcontainersConfiguration.class, KafkaTestConfig.class})
@SpringBootTest(properties = "spring.config.name=application-test")
public class KafkaConsumerServiceTest {
    @Value("${app.notifications.topic}")
    private String topic;

    @Value("${app.notifications.dlq}")
    private String dlq;

    @Autowired
    private KafkaTemplate<Long, LinkUpdate> kafkaTemplateForValidUpdate;

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplateForInvalidUpdate;

    @Autowired
    private KafkaConsumer<Long, String> kafkaConsumerForInvalidUpdate;

    @MockitoSpyBean
    private BotService botService;

    @Test
    void validMessageShouldBeProcessed() {
        // Arrange
        LinkUpdate validUpdate = new LinkUpdate(1L, "https://github.com/owner/repo", "valid", List.of(1L, 2L));

        doNothing().when(botService).updates(validUpdate);

        // Act
        kafkaTemplateForValidUpdate.send(topic, validUpdate);

        // Assert
        await().atMost(10, SECONDS).untilAsserted(() -> verify(botService)
                .updates(argThat(update -> update.id() == validUpdate.id()
                        && update.url().equals(validUpdate.url())
                        && update.description().equals(validUpdate.description())
                        && update.tgChatIds().equals(validUpdate.tgChatIds()))));
    }

    @Test
    void invalidMessageGoesToDLQ() {
        // Arrange
        String invalidUpdate = "{}";

        // Act
        kafkaTemplateForInvalidUpdate.send(topic, invalidUpdate);

        // Assert
        await().atMost(10, SECONDS)
                .untilAsserted(() -> verify(botService, never()).updates(any()));

        await().atMost(10, SECONDS).until(() -> {
            ConsumerRecords<Long, String> records = kafkaConsumerForInvalidUpdate.poll(Duration.ofMillis(100));
            Iterable<ConsumerRecord<Long, String>> recordList = records.records(dlq);
            return recordList.iterator().hasNext();
        });
    }
}
