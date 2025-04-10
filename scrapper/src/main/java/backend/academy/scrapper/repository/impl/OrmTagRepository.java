package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.TagEntity;
import backend.academy.scrapper.repository.TagRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmTagRepository extends TagRepository, JpaRepository<TagEntity, Long> {
    @Override
    @Transactional
    @Modifying
    @Query(
            """
    DELETE FROM TagEntity tagEntity
    WHERE NOT EXISTS (
        SELECT 1
        FROM ChatLinkTagEntity chatLinkTagEntity
        WHERE chatLinkTagEntity.tagEntity.id = tagEntity.id
    )
""")
    void deleteUnusedTags();
}
