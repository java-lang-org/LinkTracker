package backend.academy.scrapper;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScrapperService {
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;

    public ScrapperService(
        ChatService chatService,
        GitHubClient gitHubClient,
        BotClient botClient) {
        this.chatService = chatService;
        this.gitHubClient = gitHubClient;
        this.botClient = botClient;
    }

    @Scheduled(fixedRate = 60000)
    public void checkUpdates() {
        chatService.getLink2ChatIds().forEach(
            chatLink -> {
                Link link = chatLink.link();
                List<Long> chatIds = chatLink.chatIds();
                switch (link.linkType()) {
                    case GITHUB -> {
                        if (gitHubClient.hasRepositoryUpdated(link)) {
                            botClient.updates(link, "Updated", chatIds);
                        }
                    }
                    case STACK_OVERFLOW -> {
                        // TODO:
                    }
                }
            }
        );
    }
}
