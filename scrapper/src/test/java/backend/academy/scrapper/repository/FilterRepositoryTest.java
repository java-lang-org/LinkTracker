package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.FilterEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
class FilterRepositoryTest {
    @Autowired
    private FilterRepository filterRepository;

    @Test
    void shouldInsertAndFindFilter() {
        // Arrange
        String name = "Filter1";
        String pattern = ".*pattern.*";
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.name(name);
        filterEntity.pattern(pattern);

        // Act
        FilterEntity savedFilterEntity = filterRepository.save(filterEntity);
        Optional<FilterEntity> foundFilterEntity = filterRepository.findByNameAndPattern(name, pattern);

        // Assert
        assertThat(foundFilterEntity).isPresent();
        assertThat(foundFilterEntity.orElseThrow().name()).isEqualTo(savedFilterEntity.name());
        assertThat(foundFilterEntity.orElseThrow().pattern()).isEqualTo(savedFilterEntity.pattern());
    }

    @Test
    void shouldDeleteFilter() {
        // Arrange
        String name = "FilterToDelete";
        String pattern = ".*delete.*";
        FilterEntity filterEntity = new FilterEntity();
        filterEntity.name(name);
        filterEntity.pattern(pattern);

        filterRepository.save(filterEntity);

        // Act
        filterRepository.deleteUnusedFilters();
        Optional<FilterEntity> foundFilterEntity = filterRepository.findByNameAndPattern(name, pattern);

        // Assert
        assertThat(foundFilterEntity).isEmpty();
    }
}
