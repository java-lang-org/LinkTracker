package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.config.properties.NotificationsTopicProperties;
import backend.academy.scrapper.service.NotificationSendingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationSendingService implements NotificationSendingService {
    private final ObjectMapper objectMapper;
    private final NotificationsTopicProperties notificationsTopicProperties;
    private final KafkaTemplate<Long, String> kafkaTemplate;

    public KafkaNotificationSendingService(
            ObjectMapper objectMapper,
            NotificationsTopicProperties notificationsTopicProperties,
            KafkaTemplate<Long, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.notificationsTopicProperties = notificationsTopicProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendNotification(Link link, String description, List<Long> chatIds) {
        Optional<String> update = getUpdate(link, description, chatIds);
        update.ifPresent(s -> kafkaTemplate.send(notificationsTopicProperties.topic(), s));
    }

    private Optional<String> getUpdate(Link link, String description, List<Long> chatIds) {
        try {
            return Optional.of(
                    objectMapper.writeValueAsString(new LinkUpdate(link.hashCode(), link.url(), description, chatIds)));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
