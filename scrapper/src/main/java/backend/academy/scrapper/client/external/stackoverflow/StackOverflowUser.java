package backend.academy.scrapper.client.external.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackOverflowUser(@JsonProperty("display_name") String displayName) {}
