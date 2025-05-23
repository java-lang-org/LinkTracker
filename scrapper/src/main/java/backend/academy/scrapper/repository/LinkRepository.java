package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkEntity;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

public interface LinkRepository {
    Optional<LinkEntity> findByUrl(String url);

    LinkEntity save(LinkEntity linkEntity);

    void updateLastUpdateByUrl(String url, ZonedDateTime lastUpdate);

    void deleteUnusedLinks();

    Map<String, Long> countByType();
}
