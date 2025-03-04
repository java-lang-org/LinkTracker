package backend.academy.scrapper;

import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.internal.bot.BotClient;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScrapperService {
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    public ScrapperService(
            ChatService chatService,
            GitHubClient gitHubClient,
            StackOverflowClient stackOverflowClient,
            BotClient botClient) {
        this.chatService = chatService;
        this.gitHubClient = gitHubClient;
        this.stackOverflowClient = stackOverflowClient;
        this.botClient = botClient;
    }

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
