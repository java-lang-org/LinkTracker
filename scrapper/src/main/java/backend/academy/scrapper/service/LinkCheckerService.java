package backend.academy.scrapper.service;

import backend.academy.scrapper.Link;
import java.util.List;

public interface LinkCheckerService {
    List<String> checkLink(Link link);
}
