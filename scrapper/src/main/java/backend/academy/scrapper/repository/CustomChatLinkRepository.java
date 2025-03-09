package backend.academy.scrapper.repository;

import backend.academy.scrapper.LinkSubscriptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomChatLinkRepository {
    Page<LinkSubscriptions> findAllLinkSubscriptions(Pageable pageable);
}
