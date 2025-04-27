package backend.academy.scrapper.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
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
        assertThat(foundLinkEntity.orElseThrow().url()).isEqualTo(savedLinkEntity.url());
        assertThat(foundLinkEntity.orElseThrow().type()).isEqualTo(savedLinkEntity.type());
        assertThat(foundLinkEntity.orElseThrow().lastUpdate()).isEqualTo(savedLinkEntity.lastUpdate());
    }

    @Test
    void shouldUpdateLastUpdateTime() throws InterruptedException {
        // Arrange
        String url = "https://github.com/owner/repo";
        LinkEntity linkEntity = new LinkEntity();
        linkEntity.url(url);
        linkEntity.type(LinkType.GITHUB);
        linkEntity.lastUpdate(DateTimeUtils.now());

        linkRepository.save(linkEntity);

        // Act
        Thread.sleep(1000);
        ZonedDateTime newTime = DateTimeUtils.now();
        linkRepository.updateLastUpdateByUrl(url, newTime);

        // Assert
        LinkEntity updatedLink = linkRepository.findByUrl(url).orElseThrow();
        assertThat(updatedLink.url()).isEqualTo(linkEntity.url());
        assertThat(updatedLink.type()).isEqualTo(linkEntity.type());
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

        linkRepository.save(linkEntity);

        // Act
        linkRepository.deleteUnusedLinks();

        // Assert
        Optional<LinkEntity> foundLinkEntity = linkRepository.findByUrl(url);
        assertThat(foundLinkEntity).isEmpty();
    }
}
