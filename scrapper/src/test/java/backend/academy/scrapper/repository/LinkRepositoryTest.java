package backend.academy.scrapper.repository;

import backend.academy.scrapper.DateTimeUtils;
import backend.academy.scrapper.LinkType;
import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.entity.LinkEntity;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class LinkRepositoryTest {
    @Autowired
    private LinkRepository linkRepository;

    @Test
    void shouldInsertAndFindLink() {
        // Arrange
        String url = "https://github.com/owner/repo";
        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url(url);
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());

        // Act
        LinkEntity savedLinkEntity = linkRepository.save(linkEntity);
        Optional<LinkEntity> foundLinkEntity = linkRepository.findByUrl(url);

        // Assert
        assertThat(foundLinkEntity).isPresent();
        assertThat(foundLinkEntity.get().url()).isEqualTo(savedLinkEntity.url());
        assertThat(foundLinkEntity.get().type()).isEqualTo(savedLinkEntity.type());
        assertThat(foundLinkEntity.get().lastUpdate()).isEqualTo(savedLinkEntity.lastUpdate());
    }

    @Test
    void shouldUpdateLastUpdateTime() {
        // Arrange
        String url = "https://github.com/owner/repo";
        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url(url);
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());

        // Act
        ZonedDateTime newTime = DateTimeUtils.now();
        linkRepository.updateLastUpdateByUrl(url, newTime);

        // Assert
        LinkEntity updatedLink = linkRepository.findByUrl(url).orElseThrow();
        assertThat(updatedLink.lastUpdate()).isEqualTo(newTime);
    }

    @Test
    void shouldDeleteLink() {
        // Arrange
        String url = "https://github.com/owner/repo";
        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url(url);
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());

        // Act
        linkRepository.deleteUnusedLinks();

        // Assert
        Optional<LinkEntity> foundLinkEntity = linkRepository.findByUrl(url);
        assertThat(foundLinkEntity).isEmpty();
    }
}
