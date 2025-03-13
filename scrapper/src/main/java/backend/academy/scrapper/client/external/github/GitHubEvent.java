package backend.academy.scrapper.client.external.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record GitHubEvent(
        @JsonProperty("type") String type,
        @JsonProperty("actor") Actor actor,
        @JsonProperty("created_at") ZonedDateTime createdAt,
        @JsonProperty("payload") Payload payload) {
    public record Actor(@JsonProperty("login") String login) {}

    public record Payload(@JsonProperty("pull_request") PullRequest pullRequest, @JsonProperty("issue") Issue issue) {
        public record PullRequest(@JsonProperty("title") String title, @JsonProperty("body") String body) {}

        public record Issue(@JsonProperty("title") String title, @JsonProperty("body") String body) {}
    }
}
