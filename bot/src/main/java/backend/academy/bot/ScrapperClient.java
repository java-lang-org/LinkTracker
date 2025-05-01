package backend.academy.bot;

import backend.academy.bot.config.ScrapperConfig;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapperClient {
    private final RetryTemplate retryTemplate;
    private final RestClient restClient;
    private final ScrapperConfig scrapperConfig;

    public ResponseEntity<?> registerChat(long chatId) {
        try {
            return executeWithRetry(context -> restClient
                    .post()
                    .uri(scrapperConfig.url() + "/tg-chat/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity());
        } catch (Exception e) {
            return handleException(e, "register chat", chatId);
        }
    }

    public ResponseEntity<?> deleteChat(long chatId) {
        try {
            return executeWithRetry(context -> restClient
                    .delete()
                    .uri(scrapperConfig.url() + "/tg-chat/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity());
        } catch (Exception e) {
            return handleException(e, "delete chat", chatId);
        }
    }

    public ResponseEntity<?> setImmediate(long chatId) {
        try {
            return executeWithRetry(context -> restClient
                    .put()
                    .uri(scrapperConfig.url() + "/set-immediate/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity());
        } catch (Exception e) {
            return handleException(e, "set immediate", chatId);
        }
    }

    public ResponseEntity<?> setDigest(long chatId) {
        try {
            return executeWithRetry(context -> restClient
                    .put()
                    .uri(scrapperConfig.url() + "/set-digest/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity());
        } catch (Exception e) {
            return handleException(e, "set digest", chatId);
        }
    }

    public ResponseEntity<?> getLinks(long chatId) {
        try {
            return executeWithRetry(context -> restClient
                    .get()
                    .uri(scrapperConfig.url() + "/links")
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .retrieve()
                    .toEntity(ListLinksResponse.class));
        } catch (Exception e) {
            return handleException(e, "get links", chatId);
        }
    }

    public ResponseEntity<?> getLinksByTag(long chatId, String tagName) {
        try {
            return executeWithRetry(context -> restClient
                    .get()
                    .uri(scrapperConfig.url() + "/links/{tagName}", tagName)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .retrieve()
                    .toEntity(ListLinksResponse.class));
        } catch (Exception e) {
            return handleException(e, "get links by tag", chatId);
        }
    }

    public ResponseEntity<?> addLinkTracking(long chatId, BotState botState) {
        try {
            return executeWithRetry(context -> restClient
                    .post()
                    .uri(scrapperConfig.url() + "/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .body(new AddLinkRequest(botState.url(), botState.tags(), botState.filters()))
                    .retrieve()
                    .toEntity(LinkResponse.class));
        } catch (Exception e) {
            return handleException(e, "add link tracking", chatId);
        }
    }

    public ResponseEntity<?> removeLinkTracking(long chatId, String uri) {
        try {
            return executeWithRetry(context -> restClient
                    .method(HttpMethod.DELETE)
                    .uri(scrapperConfig.url() + "/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .body(new RemoveLinkRequest(uri))
                    .retrieve()
                    .toEntity(LinkResponse.class));
        } catch (Exception e) {
            return handleException(e, "remove link tracking", chatId);
        }
    }

    private <T, E extends Throwable> T executeWithRetry(RetryCallback<T, E> retryCallback) throws E {
        return retryTemplate.execute(retryCallback);
    }

    private HttpHeaders headersWithChatId(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }

    private ResponseEntity<?> handleException(Exception e, String action, long chatId) {
        log.error("Error {} {}: ", action, chatId, e);
        if (e instanceof HttpStatusCodeException httpEx) {
            return ResponseEntity.status(httpEx.getStatusCode()).build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
