package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LinkResponse {
    @JsonProperty("id")
    private long id;

    @NotNull(message = "url cannot be null")
    @JsonProperty("url")
    private String url;

    @NotNull(message = "tags cannot be null")
    @JsonProperty("tags")
    private List<String> tags;

    @NotNull(message = "filters cannot be null")
    @JsonProperty("filters")
    private List<String> filters;
}
