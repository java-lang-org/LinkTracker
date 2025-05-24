package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.ScrapeMetrics;
import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.service.LinkCheckerService;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkCheckerServiceImpl implements LinkCheckerService {
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final ScrapeMetrics scrapeMetrics;

    @Override
    public List<String> checkLink(Link link) {
        Timer.Sample sample = scrapeMetrics.startTimer();
        try {
            return switch (link.linkType()) {
                case GITHUB -> checkSpecificLink(gitHubClient::getRecentEvents, link);
                case STACK_OVERFLOW -> checkSpecificLink(stackOverflowClient::getRecentEvents, link);
            };
        } finally {
            scrapeMetrics.recordScrape(link.linkType().name(), sample);
        }
    }

    private List<String> checkSpecificLink(Function<Link, List<String>> function, Link link) {
        return function.apply(link);
    }
}
