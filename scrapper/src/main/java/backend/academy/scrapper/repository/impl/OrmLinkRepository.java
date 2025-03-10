package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.LinkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmLinkRepository extends LinkRepository, JpaRepository<LinkEntity, Long> {
    @Override
    @Transactional
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
