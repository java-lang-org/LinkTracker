package backend.academy.scrapper.client.external;

import backend.academy.scrapper.Link;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.client.RestClient;

@AllArgsConstructor
@Getter
public abstract class ExternalClient {
    private final String baseUrl;
    private final RestClient restClient;

    public abstract boolean hasUpdate(Link link);
}
