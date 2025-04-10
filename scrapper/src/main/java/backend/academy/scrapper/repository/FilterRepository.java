package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.FilterEntity;
import java.util.Optional;

public interface FilterRepository {
    Optional<FilterEntity> findByNameAndPattern(String name, String pattern);

    FilterEntity save(FilterEntity filterEntity);

    void deleteUnusedFilters();
}
