package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);

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
