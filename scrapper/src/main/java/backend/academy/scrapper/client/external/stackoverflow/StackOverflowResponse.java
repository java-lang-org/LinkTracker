package backend.academy.scrapper.client.external.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowResponse(@JsonProperty("items") List<StackOverflowQuestion> items) {}
