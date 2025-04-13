package backend.academy.scrapper.service;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.entity.FilterEntity;
import java.util.List;

public interface FilterService {
    List<FilterEntity> addFilters(List<String> filters);

    void deleteUnusedFilters();

    List<Long> filter(List<Long> chatIds, Link link, String description);
}
