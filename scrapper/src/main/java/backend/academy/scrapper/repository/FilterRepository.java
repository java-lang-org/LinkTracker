package backend.academy.scrapper.repository;

import backend.academy.scrapper.entity.FilterEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FilterRepository extends JpaRepository<FilterEntity, Long> {
    Optional<FilterEntity> findByNameAndPattern(String name, String pattern);

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
