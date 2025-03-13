package backend.academy.scrapper.repository;

import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.TagEntity;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class TagRepositoryTest {
    @Autowired
    private TagRepository tagRepository;

    @Test
    void shouldInsertAndFindTag() {
        // Arrange
        String tagName = "tag";
        TagEntity tagEntity = new TagEntity();
        tagEntity.name(tagName);

        // Act
        TagEntity savedTagEntity = tagRepository.save(tagEntity);
        Optional<TagEntity> foundTagEntity = tagRepository.findByName(tagName);

        // Assert
        assertThat(foundTagEntity).isPresent();
        assertThat(foundTagEntity.get().name()).isEqualTo(savedTagEntity.name());
    }

    @Test
    void shouldDeleteTag() {
        // Arrange
        String tagName = "tag";
        TagEntity tagEntity = new TagEntity();
        tagEntity.name(tagName);

        tagRepository.save(tagEntity);

        // Act
        tagRepository.deleteUnusedTags();
        Optional<TagEntity> foundTagEntity = tagRepository.findByName(tagName);

        // Assert
        assertThat(foundTagEntity).isEmpty();
    }
}
