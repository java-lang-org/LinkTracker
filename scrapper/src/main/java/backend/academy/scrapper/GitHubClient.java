package backend.academy.scrapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitHubClient {
    private final RestClient restClient;

    @Value("${github.base-url:https://api.github.com}")
    private String gitHubBaseUrl;

    public GitHubClient(@Qualifier("gitHubRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean hasRepositoryUpdated(Link link) {
        String[] parts = link.uri().getPath().split("/");
        GitHubRepoInfo repoInfo = restClient.get()
            .uri(gitHubBaseUrl + "/repos/{owner}/{repo}", parts[1], parts[2])
            .retrieve()
            .body(GitHubRepoInfo.class);

        if (repoInfo.updatedAt().isAfter(link.lastUpdate())) {
            link.lastUpdate(repoInfo.updatedAt());
            return true;
        }
        return false;
    }
}
