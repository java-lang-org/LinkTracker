package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.FilterService;
import backend.academy.scrapper.service.LinkCheckerService;
import backend.academy.scrapper.service.NotificationSendingService;
import backend.academy.scrapper.service.ScrapperService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
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
    private final ExecutorService executorService;
    private final FilterService filterService;

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
            processBatchInParallel(linkSubscriptionsPage.getContent());
            page++;
        }
    }

    private void processBatchInParallel(List<LinkSubscriptions> batch) {
        int chunkSize = (int) Math.ceil((double) batch.size() / scrapperConfig.nThreads());
        List<List<LinkSubscriptions>> partitions = partitionList(batch, chunkSize);

        List<Future<?>> futures = partitions.stream()
                .map(subBatch -> executorService.submit(() -> processBatch(subBatch)))
                .collect(Collectors.toList());

        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Error processing batch", e);
            }
        });
    }

    private static <T> List<List<T>> partitionList(List<T> list, int chunkSize) {
        return list.stream().collect(Collectors.groupingBy(i -> list.indexOf(i) / chunkSize)).values().stream()
                .toList();
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
        descriptions.forEach(description -> {
            List<Long> filteredChatIds = filterService.filter(chatIds, link, description);
            if (!filteredChatIds.isEmpty()) {
                notificationSendingService.sendNotification(link, description, filteredChatIds);
            }
        });
    }
}
