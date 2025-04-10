package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.TagEntity;
import java.util.Optional;

public interface TagRepository {
    Optional<TagEntity> findByName(String name);

    TagEntity save(TagEntity tagEntity);

    void deleteUnusedTags();
}
