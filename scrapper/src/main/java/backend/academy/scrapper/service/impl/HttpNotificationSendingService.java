package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.client.internal.bot.BotClient;
import backend.academy.scrapper.service.NotificationSendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
@RequiredArgsConstructor
public class HttpNotificationSendingService implements NotificationSendingService {
    private final BotClient botClient;

    @Override
    public void sendNotification(LinkUpdate linkUpdate) throws HttpServerErrorException {
        botClient.updates(linkUpdate);
    }
}
