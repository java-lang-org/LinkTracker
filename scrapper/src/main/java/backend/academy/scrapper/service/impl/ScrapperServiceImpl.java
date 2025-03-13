package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkCheckerService;
import backend.academy.scrapper.service.NotificationSendingService;
import backend.academy.scrapper.service.ScrapperService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ScrapperServiceImpl implements ScrapperService {
    private final ScrapperConfig scrapperConfig;
    private final ChatService chatService;
    private final LinkCheckerService linkCheckerService;
    private final NotificationSendingService notificationSendingService;

    @Override
    @Scheduled(fixedRateString = "#{${app.fixed-rate-string}}")
    public void checkUpdates() {
        processDataInBatches();
    }

    private void processDataInBatches() {
        int page = 0;
        while (true) {
            Page<LinkSubscriptions> linkSubscriptionsPage = chatService.findAllLinkSubscriptions(
                    page, scrapperConfig.dataBase().batchSize());
            if (linkSubscriptionsPage == null || linkSubscriptionsPage.isEmpty()) {
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

        List<String> descriptions = linkCheckerService.checkLink(link);
        if (!descriptions.isEmpty()) {
            chatService.updateLastUpdateByUrl(link.url(), link.lastUpdate());
        }
        descriptions.forEach(description -> notificationSendingService.sendNotification(link, description, chatIds));
    }
}
