package backend.academy.scrapper;

import backend.academy.scrapper.config.ScrapperConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataBaseRepositoryFactory {
    private final ScrapperConfig scrapperConfig;

    public <T> T getRepository(T sqlRepository, T ormRepository) {
        return switch (scrapperConfig.dataBase().accessType()) {
            case SQL -> sqlRepository;
            case ORM -> ormRepository;
        };
    }
}
