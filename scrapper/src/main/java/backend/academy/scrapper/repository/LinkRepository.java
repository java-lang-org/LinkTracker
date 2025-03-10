package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkEntity;
import java.util.Optional;

public interface LinkRepository {
    Optional<LinkEntity> findByUrl(String url);

    LinkEntity save(LinkEntity linkEntity);

    void deleteUnusedLinks();
}
