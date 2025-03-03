package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RemoveLinkRequest {
    @NotEmpty(message = "uri cannot be null")
    @JsonProperty("uri")
    private String uri;
}
