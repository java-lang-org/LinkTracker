package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    @NotEmpty(message = "description cannot be null or empty")
    @JsonProperty("description")
    private String description;

    @NotEmpty(message = "code cannot be null or empty")
    @JsonProperty("code")
    private String code;

    @NotEmpty(message = "exception-name cannot be null or empty")
    @JsonProperty("exceptionName")
    private String exceptionName;

    @NotEmpty(message = "exception-message cannot be null or empty")
    @JsonProperty("exceptionMessage")
    private String exceptionMessage;

    @NotNull(message = "stacktrace cannot be null")
    @JsonProperty("stacktrace")
    private List<String> stacktrace;
}
