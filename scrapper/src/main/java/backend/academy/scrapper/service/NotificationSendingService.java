package backend.academy.scrapper.service;

import backend.academy.scrapper.Link;
import java.util.List;

public interface NotificationSendingService {
    void sendNotification(Link link, String description, List<Long> chatIds);
}
