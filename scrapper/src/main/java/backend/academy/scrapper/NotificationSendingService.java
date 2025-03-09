package backend.academy.scrapper;

import java.util.List;

public interface NotificationSendingService {
    void sendNotification(Link link, String description, List<Long> chatIds);
}
