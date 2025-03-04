package backend.academy.scrapper.client.external.github;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.client.external.ExternalClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitHubClient extends ExternalClient {
    public GitHubClient(
            @Value("${github.base-url:https://api.github.com}") String baseUrl,
            @Qualifier("gitHubRestClient") RestClient restClient) {
        super(baseUrl, restClient);
    }

    public boolean hasUpdate(Link link) {
        String[] parts = link.uri().getPath().split("/");
        GitHubRepoInfo repoInfo = restClient()
                .get()
                .uri(baseUrl() + "/repos/{owner}/{repo}", parts[1], parts[2])
                .retrieve()
                .body(GitHubRepoInfo.class);

        if (repoInfo == null || !repoInfo.updatedAt().isAfter(link.lastUpdate())) {
            return false;
        }

        link.lastUpdate(repoInfo.updatedAt());

        return true;
    }
}
