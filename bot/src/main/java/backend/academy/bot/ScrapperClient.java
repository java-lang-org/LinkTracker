package backend.academy.bot;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ScrapperClient {
    private final RestClient restClient;

    @Value("${scrapper.url}")
    private String scrapperUrl;

    public ScrapperClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseEntity<?> registerChat(long chatId) {
        try {
            return restClient
                    .post()
                    .uri(scrapperUrl + "/tg-chat/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            return handleException(e, "register chat", chatId);
        }
    }

    public ResponseEntity<?> deleteChat(long chatId) {
        try {
            return restClient
                    .delete()
                    .uri(scrapperUrl + "/tg-chat/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            return handleException(e, "delete chat", chatId);
        }
    }

    public ResponseEntity<?> setImmediate(long chatId) {
        try {
            return restClient
                    .put()
                    .uri(scrapperUrl + "/set-immediate/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            return handleException(e, "set immediate", chatId);
        }
    }

    public ResponseEntity<?> setDigest(long chatId) {
        try {
            return restClient
                    .put()
                    .uri(scrapperUrl + "/set-digest/{id}", chatId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            return handleException(e, "set digest", chatId);
        }
    }

    public ResponseEntity<?> getLinks(long chatId) {
        try {
            return restClient
                    .get()
                    .uri(scrapperUrl + "/links")
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .retrieve()
                    .toEntity(ListLinksResponse.class);
        } catch (Exception e) {
            return handleException(e, "get links", chatId);
        }
    }

    public ResponseEntity<?> getLinksByTag(long chatId, String tagName) {
        try {
            return restClient
                    .get()
                    .uri(scrapperUrl + "/links/{tagName}", tagName)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .retrieve()
                    .toEntity(ListLinksResponse.class);
        } catch (Exception e) {
            return handleException(e, "get links by tag", chatId);
        }
    }

    public ResponseEntity<?> addLinkTracking(long chatId, BotState botState) {
        try {
            return restClient
                    .post()
                    .uri(scrapperUrl + "/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .body(new AddLinkRequest(botState.url(), botState.tags(), botState.filters()))
                    .retrieve()
                    .toEntity(LinkResponse.class);
        } catch (Exception e) {
            return handleException(e, "add link tracking", chatId);
        }
    }

    public ResponseEntity<?> removeLinkTracking(long chatId, String uri) {
        try {
            return restClient
                    .method(HttpMethod.DELETE)
                    .uri(scrapperUrl + "/links")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.addAll(headersWithChatId(chatId)))
                    .body(new RemoveLinkRequest(uri))
                    .retrieve()
                    .toEntity(LinkResponse.class);
        } catch (Exception e) {
            return handleException(e, "remove link tracking", chatId);
        }
    }

    private HttpHeaders headersWithChatId(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }

    private ResponseEntity<?> handleException(Exception e, String action, long chatId) {
        if (e instanceof HttpClientErrorException || e instanceof HttpServerErrorException) {
            HttpStatusCodeException httpEx = (HttpStatusCodeException) e;
            log.error("Error {} {}: {} - {}", action, chatId, httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
            return ResponseEntity.status(httpEx.getStatusCode()).body(httpEx.getResponseBodyAs(ApiErrorResponse.class));
        } else {
            log.error("Unexpected error while {} {}", action, chatId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
