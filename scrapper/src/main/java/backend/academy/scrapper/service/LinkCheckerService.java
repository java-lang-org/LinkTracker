package backend.academy.scrapper.service;

import backend.academy.scrapper.Link;
import java.util.Optional;

public interface LinkCheckerService {
    Optional<String> checkLink(Link link);
}
