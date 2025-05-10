package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.service.NotificationSendingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompositeNotificationSendingService implements NotificationSendingService {
    private final ScrapperConfig scrapperConfig;
    private final HttpNotificationSendingService httpNotificationSendingService;
    private final KafkaNotificationSendingService kafkaNotificationSendingService;

    @Override
    public void sendNotification(LinkUpdate linkUpdate) {
        if (scrapperConfig.messageTransport() == ScrapperConfig.MessageTransport.HTTP) {
            sendNotificationHttpWithFallback(linkUpdate);
        }

        if (scrapperConfig.messageTransport() == ScrapperConfig.MessageTransport.Kafka) {
            sendNotificationKafkaWithFallback(linkUpdate);
        }
    }

    private void sendNotificationHttpWithFallback(LinkUpdate linkUpdate) {
        try {
            httpNotificationSendingService.sendNotification(linkUpdate);
        } catch (HttpServerErrorException e) {
            logWarnHttp(linkUpdate.url(), e);
            sendNotificationKafkaWithoutFallback(linkUpdate);
        }
    }

    private void sendNotificationKafkaWithFallback(LinkUpdate linkUpdate) {
        try {
            kafkaNotificationSendingService.sendNotification(linkUpdate);
        } catch (Exception e) {
            logWarnKafka(linkUpdate.url(), e);
            sendNotificationHttpWithoutFallback(linkUpdate);
        }
    }

    private void sendNotificationHttpWithoutFallback(LinkUpdate linkUpdate) {
        try {
            httpNotificationSendingService.sendNotification(linkUpdate);
        } catch (HttpServerErrorException e) {
            logWarnHttp(linkUpdate.url(), e);
        }
    }

    private void sendNotificationKafkaWithoutFallback(LinkUpdate linkUpdate) {
        try {
            kafkaNotificationSendingService.sendNotification(linkUpdate);
        } catch (Exception e) {
            logWarnKafka(linkUpdate.url(), e);
        }
    }

    private void logWarnHttp(String url, Throwable throwable) {
        log.warn("Warning while send notification using http for link {}", url, throwable);
    }

    private void logWarnKafka(String url, Throwable throwable) {
        log.warn("Warning while send notification using kafka for link {}", url, throwable);
    }
}
