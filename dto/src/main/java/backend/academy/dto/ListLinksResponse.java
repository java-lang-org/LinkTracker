package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListLinksResponse {
    @NotNull(message = "links cannot be null")
    @JsonProperty("links")
    private List<LinkResponse> links;

    @Size
    @JsonProperty("size")
    private int size;
}
