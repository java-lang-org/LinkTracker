package backend.academy.scrapper.service.impl;

import backend.academy.dto.LinkUpdate;
import backend.academy.scrapper.DigestNotification;
import backend.academy.scrapper.Link;
import backend.academy.scrapper.NotificationMode;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.service.NotificationSendingService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigestService {
    private final RedisTemplate<String, DigestNotification> redisTemplate;
    private final NotificationSendingService notificationSendingService;

    @Value("${app.digest.prefix-key:digest}")
    private String keyPrefix;

    public void handleDigestNotification(Link link, String description, List<ChatEntity> chatEntities) {
        List<Long> digestChatIds = chatEntities.stream()
                .filter(chatEntity -> chatEntity.notificationMode() == NotificationMode.DIGEST)
                .map(ChatEntity::id)
                .toList();
        if (!digestChatIds.isEmpty()) {
            redisTemplate
                    .opsForList()
                    .rightPush(keyPrefix + link.url(), new DigestNotification(description, digestChatIds));
        }
    }

    @Scheduled(cron = "${app.digest.cron:0 0 10 * * *}")
    public void sendDigestNotifications() {
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            String url = key.replaceFirst(keyPrefix, "");

            List<DigestNotification> digestNotifications =
                    redisTemplate.opsForList().range(key, 0, -1);
            if (digestNotifications == null) {
                continue;
            }

            for (DigestNotification digestNotification : digestNotifications) {
                if (digestNotification == null) {
                    continue;
                }

                notificationSendingService.sendNotification(new LinkUpdate(
                        url.hashCode(), url, digestNotification.description(), digestNotification.chatIds()));
            }
            redisTemplate.delete(key);
        }
    }
}
