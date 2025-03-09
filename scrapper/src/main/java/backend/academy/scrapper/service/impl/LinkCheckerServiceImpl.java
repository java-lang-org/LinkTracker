package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import backend.academy.scrapper.service.LinkCheckerService;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkCheckerServiceImpl implements LinkCheckerService {
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;

    @Override
    public Optional<String> checkLink(Link link) {
        return switch (link.linkType()) {
            case GITHUB -> checkSpecificLink(gitHubClient::hasUpdate, link);
            case STACK_OVERFLOW -> checkSpecificLink(stackOverflowClient::hasUpdate, link);
        };
    }

    private Optional<String> checkSpecificLink(Function<Link, Boolean> function, Link link) {
        return function.apply(link) ? Optional.of("...") : Optional.empty();
    }
}
