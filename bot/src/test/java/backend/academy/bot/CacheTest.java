package backend.academy.bot;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = "spring.config.name=application-test")
public class CacheTest {
    @MockitoSpyBean
    private ScrapperClient scrapperClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CachedCommandService cachedCommandService;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("listLinksCache").clear();
    }

    @AfterEach
    void tearDown() {
        clearInvocations(scrapperClient);
        reset(scrapperClient);
    }

    @Test
    void handleListCommand_shouldUseCache() {
        // Arrange
        long chatId = 1L;
        LinkResponse link1 = new LinkResponse(1L, "https://github.com/owner1/repo1", List.of(), List.of());
        LinkResponse link2 = new LinkResponse(1L, "https://github.com/owner2/repo2", List.of(), List.of());

        doReturn(ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(link1, link2), 1)))
                .when(scrapperClient)
                .getLinks(chatId);

        // Act
        String noCachedRes = cachedCommandService.handleListCommand(chatId);
        String cachedRes = cachedCommandService.handleListCommand(chatId);

        // Assert
        assertThat(noCachedRes).isEqualTo(cachedRes);
        verify(scrapperClient, times(1)).getLinks(chatId);
    }

    @Test
    void handleListCommand_shouldNotUseCache() {
        // Arrange
        long chatId = 1L;
        String url1 = "https://github.com/owner1/repo1";
        LinkResponse link1 = new LinkResponse(1L, url1, List.of(), List.of());
        LinkResponse link2 = new LinkResponse(1L, "https://github.com/owner2/repo2", List.of(), List.of());

        ResponseEntity<?> firstResponse =
                ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(link1, link2), 1));
        ResponseEntity<?> secondResponse =
                ResponseEntity.status(HttpStatus.OK).body(new ListLinksResponse(List.of(link2), 1));
        doReturn(firstResponse, secondResponse).when(scrapperClient).getLinks(chatId);

        // Act
        String noCachedRes1 = cachedCommandService.handleListCommand(chatId);
        cachedCommandService.handleUntrackedUrl(chatId, url1);
        String noCachedRes2 = cachedCommandService.handleListCommand(chatId);

        // Assert
        assertThat(noCachedRes1).isNotEqualTo(noCachedRes2);
        verify(scrapperClient, times(2)).getLinks(chatId);
    }
}
