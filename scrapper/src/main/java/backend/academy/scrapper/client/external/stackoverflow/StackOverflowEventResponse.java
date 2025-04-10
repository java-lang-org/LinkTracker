package backend.academy.scrapper.client.external.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowEventResponse(@JsonProperty("items") List<StackOverflowEvent> items) {}
