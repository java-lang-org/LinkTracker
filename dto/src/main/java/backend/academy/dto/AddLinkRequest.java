package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddLinkRequest {
    @NotNull(message = "uri cannot be null")
    @JsonProperty("uri")
    private String url;

    @NotNull(message = "tags cannot be null")
    @JsonProperty("tags")
    private List<String> tags;

    @NotNull(message = "filters cannot be null")
    @JsonProperty("filters")
    private List<String> filters;
}
