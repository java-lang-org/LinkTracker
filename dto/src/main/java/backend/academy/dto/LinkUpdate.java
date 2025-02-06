package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class LinkUpdate {
    @NotNull(message = "id cannot be null")
    @JsonProperty("id")
    private long id;

    @NotNull(message = "url cannot be null")
    @JsonProperty("url")
    private String url;

    @NotNull(message = "description cannot be null")
    @Size(max = 500, message = "description should not exceed 500 characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "tgChatIds cannot be null")
    @Size(min = 1, message = "At least one thChatId is required")
    @JsonProperty("tgChatIds")
    private List<Long> tgChatIds;
}
