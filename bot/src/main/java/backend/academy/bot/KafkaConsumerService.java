package backend.academy.bot;

import backend.academy.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final BotService botService;

    @KafkaListener(topics = "${app.notifications.topic}", groupId = "${app.notifications.group-id}")
    public void listen(LinkUpdate linkUpdate) {
        botService.updates(linkUpdate);
    }
}
