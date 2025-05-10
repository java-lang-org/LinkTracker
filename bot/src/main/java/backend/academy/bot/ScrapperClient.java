package backend.academy.bot;

import backend.academy.bot.config.ScrapperConfig;
import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapperClient {
    private final RetryTemplate retryTemplate;
    private final RestClient restClient;
    private final ScrapperConfig scrapperConfig;

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "registerChatFallback")
    public ResponseEntity<?> registerChat(long chatId) {
        return executeWithRetry(context -> restClient
                .post()
                .uri(scrapperConfig.url() + "/tg-chat/{id}", chatId)
                .retrieve()
                .toBodilessEntity());
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "deleteChatFallback")
    public ResponseEntity<?> deleteChat(long chatId) {
        return executeWithRetry(context -> restClient
                .delete()
                .uri(scrapperConfig.url() + "/tg-chat/{id}", chatId)
                .retrieve()
                .toBodilessEntity());
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "setImmediateFallback")
    public ResponseEntity<?> setImmediate(long chatId) {
        return executeWithRetry(context -> restClient
                .put()
                .uri(scrapperConfig.url() + "/set-immediate/{id}", chatId)
                .retrieve()
                .toBodilessEntity());
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "setDigestFallback")
    public ResponseEntity<?> setDigest(long chatId) {
        return executeWithRetry(context -> restClient
                .put()
                .uri(scrapperConfig.url() + "/set-digest/{id}", chatId)
                .retrieve()
                .toBodilessEntity());
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "getLinksFallback")
    public ResponseEntity<?> getLinks(long chatId) {
        return executeWithRetry(context -> restClient
                .get()
                .uri(scrapperConfig.url() + "/links")
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .retrieve()
                .toEntity(ListLinksResponse.class));
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "getLinksByTagFallback")
    public ResponseEntity<?> getLinksByTag(long chatId, String tagName) {
        return executeWithRetry(context -> restClient
                .get()
                .uri(scrapperConfig.url() + "/links/{tagName}", tagName)
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .retrieve()
                .toEntity(ListLinksResponse.class));
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "addLinkTrackingFallback")
    public ResponseEntity<?> addLinkTracking(long chatId, BotState botState) {
        return executeWithRetry(context -> restClient
                .post()
                .uri(scrapperConfig.url() + "/links")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .body(new AddLinkRequest(botState.url(), botState.tags(), botState.filters()))
                .retrieve()
                .toEntity(LinkResponse.class));
    }

    @CircuitBreaker(name = "scrapper-client", fallbackMethod = "removeLinkTrackingFallback")
    public ResponseEntity<?> removeLinkTracking(long chatId, String uri) {
        return executeWithRetry(context -> restClient
                .method(HttpMethod.DELETE)
                .uri(scrapperConfig.url() + "/links")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                .body(new RemoveLinkRequest(uri))
                .retrieve()
                .toEntity(LinkResponse.class));
    }

    private <T, E extends Throwable> T executeWithRetry(RetryCallback<T, E> retryCallback) throws E {
        return retryTemplate.execute(retryCallback);
    }

    private HttpHeaders headersWithChatId(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    public ResponseEntity<?> registerChatFallback(long chatId, Throwable throwable) {
        log.warn("Warning while executing \"register chat\" for chat id {}", chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    public ResponseEntity<?> deleteChatFallback(long chatId, Throwable throwable) {
        log.warn("Warning while executing \"delete chat\" for chat id {}", chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    public ResponseEntity<?> setImmediateFallback(long chatId, Throwable throwable) {
        log.warn("Warning while executing \"set immediate\" for chat id {}", chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    public ResponseEntity<?> setDigestFallback(long chatId, Throwable throwable) {
        log.warn("Warning while executing \"set digest\" for chat id {}", chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ResponseEntity<?> getLinksFallback(long chatId, Throwable throwable) {
        log.warn("Warning while executing \"get links\" for chat id {}", chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ResponseEntity<?> getLinksByTagFallback(long chatId, String tagName, Throwable throwable) {
        log.warn("Warning while executing \"get links by tag {}\" for chat id {}", tagName, chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ResponseEntity<?> addLinkTrackingFallback(long chatId, BotState botState, Throwable throwable) {
        log.warn("Warning while executing \"add link {} tracking\" for chat id {}", botState.url(), chatId, throwable);
        return fallback();
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private ResponseEntity<?> removeLinkTrackingFallback(long chatId, String uri, Throwable throwable) {
        log.warn("Warning while executing \"remove link {} tracking\" for chat id {}", uri, chatId, throwable);
        return fallback();
    }

    private ResponseEntity<?> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE.value()).build();
    }
}
