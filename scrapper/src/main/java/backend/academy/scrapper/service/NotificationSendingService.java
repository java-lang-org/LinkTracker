package backend.academy.scrapper.service;

import backend.academy.dto.LinkUpdate;

public interface NotificationSendingService {
    void sendNotification(LinkUpdate linkUpdate);
}
