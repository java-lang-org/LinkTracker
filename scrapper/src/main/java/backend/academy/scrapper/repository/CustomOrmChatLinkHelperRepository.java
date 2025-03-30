package backend.academy.scrapper.repository;

import backend.academy.scrapper.LinkSubscriptions;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomOrmChatLinkHelperRepository {
    List<LinkSubscriptions> fetchLinkSubscriptions(Pageable pageable);

    long getTotalCount();
}
