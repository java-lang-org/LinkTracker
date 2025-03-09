package backend.academy.scrapper;

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
    private final LinkCheckerService linkCheckerService;
    private final NotificationSendingService notificationSendingService;

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
        Link link = linkSubscription.link();
        List<Long> chatIds = linkSubscription.chatIds();

        log.info(
                "Processing link: url={}, type={}, lastUpdate={}, chatIds={}",
                link.url(),
                link.linkType(),
                link.lastUpdate(),
                chatIds);

        linkCheckerService
                .checkLink(link)
                .ifPresent(description -> notificationSendingService.sendNotification(link, description, chatIds));
    }
}
