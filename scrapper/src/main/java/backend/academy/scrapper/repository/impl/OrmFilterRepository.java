package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.repository.FilterRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmFilterRepository extends FilterRepository, JpaRepository<FilterEntity, Long> {
    @Override
    @Transactional
    @Modifying
    @Query(
            """
        DELETE FROM FilterEntity filterEntity
        WHERE NOT EXISTS (
            SELECT 1
            FROM ChatLinkFilterEntity chatLinkFilterEntity
            WHERE chatLinkFilterEntity.filterEntity.id = filterEntity.id
        )
    """)
    void deleteUnusedFilters();
}
