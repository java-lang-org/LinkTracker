package backend.academy.scrapper.client.external;

import backend.academy.scrapper.Link;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestClient;

@AllArgsConstructor
public abstract class ExternalClient {
    private static final int PREVIEW_LENGTH = 200;

    private final String baseUrl;
    private final RestClient restClient;

    public String baseUrl() {
        return baseUrl;
    }

    public RestClient restClient() {
        return restClient;
    }

    public abstract List<String> getRecentEvents(Link link);

    protected String truncate(String text) {
        return text.length() > PREVIEW_LENGTH ? text.substring(0, PREVIEW_LENGTH) + "..." : text;
    }
}
