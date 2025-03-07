package backend.academy.scrapper;

import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.internal.bot.BotClient;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScrapperService {
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Scheduled(fixedRate = 3_600_000) // 1 hour
    public void checkUpdates() {
        chatService.getLink2ChatIds().forEach(chatLink -> {
            Link link = chatLink.link();
            List<Long> chatIds = chatLink.chatIds();
            switch (link.linkType()) {
                case GITHUB -> {
                    if (gitHubClient.hasUpdate(link)) {
                        botClient.updates(link, "...", chatIds);
                    }
                }
                case STACK_OVERFLOW -> {
                    if (stackOverflowClient.hasUpdate(link)) {
                        botClient.updates(link, "...", chatIds);
                    }
                }
            }
        });
    }
}
