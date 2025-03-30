package backend.academy.scrapper.repository.impl;

import backend.academy.scrapper.LinkSubscriptions;
import backend.academy.scrapper.repository.CustomOrmChatLinkHelperRepository;
import backend.academy.scrapper.repository.CustomOrmChatLinkRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomOrmChatLinkRepositoryImpl implements CustomOrmChatLinkRepository {
    private CustomOrmChatLinkHelperRepository customOrmChatLinkHelperRepository;

    @Override
    public Page<LinkSubscriptions> findAllLinkSubscriptions(Pageable pageable) {
        List<LinkSubscriptions> subscriptions = customOrmChatLinkHelperRepository.fetchLinkSubscriptions(pageable);
        long total = customOrmChatLinkHelperRepository.getTotalCount();
        return new PageImpl<>(subscriptions, pageable, total);
    }
}
