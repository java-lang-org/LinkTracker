package backend.academy.scrapper.client.external.github;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.client.external.ExternalClient;
import backend.academy.scrapper.config.GitHubConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class GitHubClient extends ExternalClient {
    private final RetryTemplate retryTemplate;

    public GitHubClient(
            GitHubConfig gitHubConfig,
            @Qualifier("gitHubRestClient") RestClient restClient,
            RetryTemplate retryTemplate) {
        super(gitHubConfig.baseUrl(), restClient);

        this.retryTemplate = retryTemplate;
    }

    @Override
    @CircuitBreaker(name = "github-client", fallbackMethod = "getRecentEventsFallback")
    public List<String> getRecentEvents(Link link) {
        String[] parts = link.uri().getPath().split("/");
        GitHubEvent[] events = retryTemplate.execute(context -> restClient()
                .get()
                .uri(baseUrl() + "/repos/{owner}/{repo}/events", parts[1], parts[2])
                .retrieve()
                .body(GitHubEvent[].class));

        return Arrays.stream(events)
                .filter(event -> link.lastUpdate().isBefore(event.createdAt()))
                .filter(event -> "PullRequestEvent".equals(event.type()) || "IssuesEvent".equals(event.type()))
                .peek(event -> {
                    if (link.lastUpdate().isBefore(event.createdAt())) {
                        link.lastUpdate(event.createdAt());
                    }
                })
                .map(this::formatEventMessage)
                .collect(Collectors.toList());
    }

    private String formatEventMessage(GitHubEvent event) {
        Optional<String> title = Optional.ofNullable(event.payload())
                .map(p -> p.pullRequest() != null
                        ? p.pullRequest().title()
                        : p.issue() != null ? p.issue().title() : null);

        Optional<String> description = Optional.ofNullable(event.payload())
                .map(p -> p.pullRequest() != null
                        ? p.pullRequest().body()
                        : p.issue() != null ? p.issue().body() : null);

        return title.map(t -> String.format(
                        "**%s** (by %s)%n%s%n%s",
                        t,
                        event.actor().login(),
                        event.createdAt(),
                        description.map(this::truncate).orElse("")))
                .orElse("");
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private List<String> getRecentEventsFallback(Link link, Throwable throwable) {
        log.warn("Warning while executing \"get recent events for github client\" for link {}", link.url(), throwable);
        return List.of();
    }
}
