package backend.academy.bot;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
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
public class ApiErrorResponse {
    @NotNull(message = "description cannot be null")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "code cannot be null")
    @JsonProperty("code")
    private String code;

    @NotNull(message = "exceptionName cannot be null")
    @JsonProperty("exceptionName")
    private String exceptionName;

    @NotNull(message = "exceptionMessage cannot be null")
    @JsonProperty("exceptionMessage")
    private String exceptionMessage;

    @NotNull(message = "stacktrace cannot be null")
    @JsonProperty("stacktrace")
    private List<String> stacktrace;
}
