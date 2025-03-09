package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.service.FilterService;
import java.util.List;

public class SqlFilterService implements FilterService {
    @Override
    public List<FilterEntity> addFilters(List<String> filters) {
        return List.of();
    }

    @Override
    public void deleteUnusedFilters() {}
}
