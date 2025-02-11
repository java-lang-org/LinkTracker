package backend.academy.scrapper;

import java.util.List;

public record ChatLink(Link link, List<Long> chatIds) {
}
