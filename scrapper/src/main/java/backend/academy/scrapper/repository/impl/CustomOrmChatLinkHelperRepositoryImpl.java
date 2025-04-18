package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.entity.ChatEntity;
import backend.academy.scrapper.repository.CustomOrmChatLinkHelperRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            chatLinkEntity.chatEntity
        FROM LinkEntity linkEntity
        JOIN ChatLinkEntity chatLinkEntity ON chatLinkEntity.linkEntity.id = linkEntity.id
    """;

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query, Object[].class);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return convert(typedQuery.getResultList());
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

    private List<LinkSubscriptions> convert(List<Object[]> results) {
        Map<String, LinkSubscriptions> urlToSubscription = new HashMap<>();

        for (Object[] row : results) {
            String url = (String) row[0];
            LinkType type = (LinkType) row[1];
            ZonedDateTime update = (ZonedDateTime) row[2];
            ChatEntity chat = (ChatEntity) row[3];

            urlToSubscription
                    .computeIfAbsent(url, k -> new LinkSubscriptions(url, type, update, new ArrayList<>()))
                    .chatIds()
                    .add(chat);
        }

        return new ArrayList<>(urlToSubscription.values());
    }
}
