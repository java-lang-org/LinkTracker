package backend.academy.scrapper;

import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.client.internal.bot.BotClient;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ScrapperService {
    private final ScrapperConfig scrapperConfig;
    private final ChatService chatService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Scheduled(fixedRateString = "#{${app.fixed-rate-string}}")
    public void checkUpdates() {
        processDataInBatches();
    }

    private void processDataInBatches() {
        int page = 0;
        while (true) {
            Page<LinkSubscriptions> linkSubscriptionsPage =
                    chatService.findAllLinkSubscriptions(page, scrapperConfig.batchSize());
            if (linkSubscriptionsPage.isEmpty()) {
                break;
            }

            processBatch(linkSubscriptionsPage.getContent());
            page++;
        }
    }

    private void processBatch(List<LinkSubscriptions> batch) {
        batch.forEach(this::processLinkSubscription);
    }

    private void processLinkSubscription(LinkSubscriptions linkSubscription) {
        log.info(
                "Processing link: url={}, type={}, lastUpdate={}, chatIds={}",
                linkSubscription.link().url(),
                linkSubscription.link().linkType(),
                linkSubscription.link().lastUpdate(),
                linkSubscription.chatIds());

        Link link = linkSubscription.link();
        List<Long> chatIds = linkSubscription.chatIds();

        switch (link.linkType()) {
            case GITHUB -> processGitHubLink(link, chatIds);
            case STACK_OVERFLOW -> processStackOverflowLink(link, chatIds);
            default -> log.warn("Unsupported link type: {}", link.linkType());
        }
    }

    private void processGitHubLink(Link link, List<Long> chatIds) {
        if (gitHubClient.hasUpdate(link)) {
            botClient.updates(link, "...", chatIds);
        }
    }

    private void processStackOverflowLink(Link link, List<Long> chatIds) {
        if (stackOverflowClient.hasUpdate(link)) {
            botClient.updates(link, "...", chatIds);
        }
    }
}
