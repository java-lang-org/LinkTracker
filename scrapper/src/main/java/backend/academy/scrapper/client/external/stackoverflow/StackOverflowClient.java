package backend.academy.scrapper.client.external.stackoverflow;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.external.ExternalClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StackOverflowClient extends ExternalClient {
    private final ScrapperConfig scrapperConfig;

    public StackOverflowClient(
            @Value("${stackoverflow.base-url:https://api.stackexchange.com/2.3}") String baseUrl,
            @Qualifier("stackOverflowRestClient") RestClient restClient,
            ScrapperConfig scrapperConfig) {
        super(baseUrl, restClient);

        this.scrapperConfig = scrapperConfig;
    }

    public boolean hasUpdate(Link link) {
        String[] parts = link.uri().getPath().split("/");
        String url = UriComponentsBuilder.fromUriString(baseUrl())
                .path("/questions/{ids}")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .buildAndExpand(parts[2])
                .toUriString();

        StackOverflowResponse stackOverflowResponse =
                restClient().get().uri(url).retrieve().body(StackOverflowResponse.class);

        if (stackOverflowResponse == null || stackOverflowResponse.items().size() != 1) {
            return false;
        }

        StackOverflowQuestion stackOverflowQuestion =
                stackOverflowResponse.items().getFirst();
        if (stackOverflowQuestion.lastActivityDate().isAfter(link.lastUpdate())) {
            link.lastUpdate(stackOverflowQuestion.lastActivityDate());
            return true;
        }

        return false;
    }
}
