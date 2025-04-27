package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LinkUpdate implements Serializable {
    @NotNull(message = "id cannot be null")
    @JsonProperty("id")
    private long id;

    @NotEmpty(message = "url cannot be null or empty")
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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof LinkUpdate that)) {
            return false;
        }

        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
