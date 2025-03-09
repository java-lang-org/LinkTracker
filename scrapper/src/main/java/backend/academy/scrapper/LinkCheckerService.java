package backend.academy.scrapper;

import backend.academy.scrapper.client.external.github.GitHubClient;
import backend.academy.scrapper.client.external.stackoverflow.StackOverflowClient;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LinkCheckerService {
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;

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
