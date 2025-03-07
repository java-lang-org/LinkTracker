package backend.academy.scrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;

@Getter
public class LinkWithTagsAndFilters {
    private final String url;
    private final List<String> tags;
    private final List<String> filters;

    public LinkWithTagsAndFilters(String url, Object tags, Object filters) {
        this.url = url;
        this.tags = parseObjectToList(tags);
        this.filters = parseObjectToList(filters);
    }

    private List<String> parseObjectToList(Object object) {
        return Optional.ofNullable(object)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.asList(s.split(" ")))
                .orElse(List.of());
    }
}
