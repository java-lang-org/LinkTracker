package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.client.internal.bot.BotClient;
import backend.academy.scrapper.service.NotificationSendingService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HttpNotificationSendingService implements NotificationSendingService {
    private final BotClient botClient;

    @Override
    public void sendNotification(Link link, String description, List<Long> chatIds) {
        botClient.updates(link, description, chatIds);
    }
}
