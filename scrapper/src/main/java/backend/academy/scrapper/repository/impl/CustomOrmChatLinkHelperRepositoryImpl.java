package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.repository.CustomOrmChatLinkHelperRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomOrmChatLinkHelperRepositoryImpl implements CustomOrmChatLinkHelperRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<LinkSubscriptions> fetchLinkSubscriptions(Pageable pageable) {
        String query =
                """
        SELECT
            linkEntity.url,
            linkEntity.type,
            linkEntity.lastUpdate,
            ARRAY_AGG(chatLinkEntity.chatEntity.id) WITHIN GROUP (ORDER BY chatLinkEntity.chatEntity.id)
        FROM LinkEntity linkEntity
        JOIN ChatLinkEntity chatLinkEntity
            ON chatLinkEntity.linkEntity.id = linkEntity.id
        GROUP BY linkEntity.url, linkEntity.type, linkEntity.lastUpdate
    """;

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query, Object[].class);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Object[]> results = typedQuery.getResultList();

        return results.stream()
                .map(row -> new LinkSubscriptions(
                        (String) row[0], (LinkType) row[1], (ZonedDateTime) row[2], Arrays.asList((Long[]) row[3])))
                .toList();
    }

    @Override
    public long getTotalCount() {
        String countQuery =
                """
        SELECT COUNT(DISTINCT linkEntity.id)
        FROM LinkEntity linkEntity
        JOIN ChatLinkEntity chatLinkEntity
            ON chatLinkEntity.linkEntity.id = linkEntity.id
    """;
        return (long) entityManager.createQuery(countQuery).getSingleResult();
    }
}
