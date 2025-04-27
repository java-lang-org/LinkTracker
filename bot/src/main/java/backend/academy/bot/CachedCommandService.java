package backend.academy.bot;

import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CachedCommandService {
    private final ScrapperClient scrapperClient;

    @CacheEvict(
            cacheNames = {"listLinksCache", "listLinksByTagCache"},
            key = "#chatId")
    public String handleTrack(long chatId, BotState botState) {
        ResponseEntity<?> response = scrapperClient.addLinkTracking(chatId, botState);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link is tracked!";
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    @CacheEvict(
            cacheNames = {"listLinksCache", "listLinksByTagCache"},
            key = "#chatId")
    public String handleUntrackedUrl(long chatId, String url) {
        ResponseEntity<?> response = scrapperClient.removeLinkTracking(chatId, url);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link is untracked!";
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    @Cacheable(value = "listLinksByTagCache", key = "#chatId")
    public String handleTag(long chatId, String tagName) {
        ResponseEntity<?> response = scrapperClient.getLinksByTag(chatId, tagName);
        if (response.getStatusCode() == HttpStatus.OK) {
            ListLinksResponse listLinksResponse = (ListLinksResponse) response.getBody();

            if (listLinksResponse == null || listLinksResponse.links().isEmpty()) {
                return "No tracked links.";
            }

            return listLinksResponse.links().stream()
                    .map(LinkResponse::toString)
                    .collect(Collectors.joining("\n"));
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    @CacheEvict(
            cacheNames = {"listLinksCache", "listLinksByTagCache"},
            key = "#chatId")
    public String handleEndCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.deleteChat(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Bye! You have successfully unregistered.";
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    @Cacheable(value = "listLinksCache", key = "#chatId")
    public String handleListCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            ListLinksResponse listLinksResponse = (ListLinksResponse) response.getBody();

            if (listLinksResponse == null || listLinksResponse.links().isEmpty()) {
                return "No tracked links.";
            }

            return listLinksResponse.links().stream()
                    .map(LinkResponse::toString)
                    .collect(Collectors.joining("\n"));
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }
}
