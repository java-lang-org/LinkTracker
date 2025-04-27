package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.config.properties.NotificationsTopicProperties;
import backend.academy.scrapper.service.NotificationSendingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void sendNotification(LinkUpdate linkUpdate) {
        Optional<String> update = getUpdate(linkUpdate);
        update.ifPresent(s -> kafkaTemplate.send(notificationsTopicProperties.topic(), s));
    }

    private Optional<String> getUpdate(LinkUpdate linkUpdate) {
        try {
            return Optional.of(objectMapper.writeValueAsString(linkUpdate));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
