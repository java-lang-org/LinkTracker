package backend.academy.scrapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class StackOverflowClient {
    private final ScrapperConfig scrapperConfig;
    private final RestClient restClient;

    @Value("${stackoverflow.base-url:https://api.stackexchange.com/2.3}")
    private String stackOverflowBaseUrl;

    public StackOverflowClient(
        ScrapperConfig scrapperConfig,
        @Qualifier("stackOverflowRestClient") RestClient restClient
    ) {
        this.scrapperConfig = scrapperConfig;
        this.restClient = restClient;
    }

    public boolean hasRepositoryUpdated(Link link) {
        String[] parts = link.uri().getPath().split("/");
        String url = UriComponentsBuilder.fromUriString(stackOverflowBaseUrl)
            .path("/questions/{ids}")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .buildAndExpand(parts[2])
            .toUriString();

        StackOverflowResponse stackOverflowResponse = restClient.get()
            .uri(url)
            .retrieve()
            .body(StackOverflowResponse.class);

        if (stackOverflowResponse.items().size() != 1) {
            return false;
        }

        StackOverflowQuestion stackOverflowQuestion = stackOverflowResponse.items().getFirst();
        if (stackOverflowQuestion.lastActivityDate().isAfter(link.lastUpdate())) {
            link.lastUpdate(stackOverflowQuestion.lastActivityDate());
            return true;
        }

        return false;
    }
}
