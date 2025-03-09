package backend.academy.scrapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataBaseServiceFactory {
    private final ScrapperConfig scrapperConfig;

    public <T> T getService(T sqlService, T ormService) {
        return switch (scrapperConfig.dataBase().accessType()) {
            case SQL -> sqlService;
            case ORM -> ormService;
        };
    }
}
