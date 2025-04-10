package backend.academy.scrapper.client.external.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record StackOverflowEvent(
        @JsonProperty("owner") StackOverflowUser owner,
        @JsonProperty("creation_date") ZonedDateTime creationDate,
        @JsonProperty("body") String body) {

    public String ownerName() {
        return owner.displayName();
    }
}
