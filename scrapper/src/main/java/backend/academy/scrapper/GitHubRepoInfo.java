package backend.academy.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record GitHubRepoInfo(
    @JsonProperty("full_name") String fullName,
    @JsonProperty("updated_at") ZonedDateTime updatedAt
) {}
