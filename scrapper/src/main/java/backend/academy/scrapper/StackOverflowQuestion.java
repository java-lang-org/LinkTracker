package backend.academy.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record StackOverflowQuestion(
        @JsonProperty("question_id") long questionId,
        @JsonProperty("title") String title,
        @JsonProperty("last_activity_date") ZonedDateTime lastActivityDate) {}
