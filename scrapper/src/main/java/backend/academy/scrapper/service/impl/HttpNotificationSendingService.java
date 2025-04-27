package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.client.internal.bot.BotClient;
import backend.academy.scrapper.service.NotificationSendingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HttpNotificationSendingService implements NotificationSendingService {
    private final BotClient botClient;

    @Override
    public void sendNotification(LinkUpdate linkUpdate) {
        botClient.updates(linkUpdate);
    }
}
