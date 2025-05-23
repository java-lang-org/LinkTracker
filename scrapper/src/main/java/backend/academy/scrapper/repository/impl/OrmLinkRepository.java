package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.entity.LinkEntity;
import backend.academy.scrapper.repository.LinkRepository;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrmLinkRepository extends LinkRepository, JpaRepository<LinkEntity, Long> {
    @Override
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE LinkEntity linkEntity SET linkEntity.lastUpdate = :lastUpdate WHERE linkEntity.url = :url")
    void updateLastUpdateByUrl(@Param("url") String url, @Param("lastUpdate") ZonedDateTime lastUpdate);

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

    @Transactional
    @Query("SELECT linkEntity.type, COUNT(linkEntity) FROM LinkEntity linkEntity GROUP BY linkEntity.type")
    List<Object[]> countByTypeRaw();

    @Override
    @Transactional
    default Map<String, Long> countByType() {
        return countByTypeRaw().stream()
                .collect(Collectors.toMap(row -> ((LinkType) row[0]).name(), row -> (Long) row[1]));
    }
}
