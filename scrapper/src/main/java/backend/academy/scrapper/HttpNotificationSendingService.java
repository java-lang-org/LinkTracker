package backend.academy.scrapper;

import backend.academy.scrapper.client.internal.bot.BotClient;
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
