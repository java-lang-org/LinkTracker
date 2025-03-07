package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.LinkEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);

    @Modifying
    @Query(
            """
        DELETE FROM LinkEntity linkEntity
        WHERE NOT EXISTS (
            SELECT 1
            FROM ChatLinkEntity chatLinkEntity
            WHERE chatLinkEntity.linkEntity.id = linkEntity.id
        )
    """)
    void deleteUnusedLinks();
}
