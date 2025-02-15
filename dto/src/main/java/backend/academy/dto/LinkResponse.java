package backend.academy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LinkResponse {
    @JsonProperty("id")
    private long id;

    @NotEmpty(message = "url cannot be null or empty")
    @JsonProperty("url")
    private String url;

    @NotNull(message = "tags cannot be null")
    @JsonProperty("tags")
    private List<String> tags;

    @NotNull(message = "filters cannot be null")
    @JsonProperty("filters")
    private List<String> filters;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Link:\n");

        appendField(sb, "url", url);
        appendList(sb, "tags", tags);
        appendList(sb, "filters", filters);

        return sb.toString();
    }

    private void appendField(StringBuilder sb, String name, String field) {
        sb.append('\t').append(name).append(": ").append(field).append('\n');
    }

    private void appendList(StringBuilder sb, String name, List<String> elements) {
        if (!elements.isEmpty()) {
            sb.append('\t')
                    .append(name)
                    .append(": ")
                    .append(String.join(", ", elements))
                    .append('\n');
        }
    }
}
