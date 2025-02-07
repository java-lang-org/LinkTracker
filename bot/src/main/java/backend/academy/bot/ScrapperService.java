package backend.academy.bot;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ScrapperService {
    private final RestTemplate restTemplate;

    @Value("${scrapper.url}")
    private String scrapperUrl;

    public ScrapperService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<ApiErrorResponse> registerChat(long chatId) {
        return modifyChat(chatId, HttpMethod.POST);
    }

    public ResponseEntity<ApiErrorResponse> deleteChat(long chatId) {
        return modifyChat(chatId, HttpMethod.DELETE);
    }

    private ResponseEntity<ApiErrorResponse> modifyChat(long chatId, HttpMethod method) {
        return restTemplate.exchange(
            scrapperUrl + "/tg-chat?id=" + chatId,
            method,
            new HttpEntity<>(createHeaders()),
            ApiErrorResponse.class
        );
    }

    public ResponseEntity<ListLinksResponse> getLinks(long chatId) {
        return restTemplate.exchange(
            scrapperUrl + "/links",
            HttpMethod.GET,
            new HttpEntity<>(createHeaders(chatId)),
            ListLinksResponse.class
        );
    }

    public ResponseEntity<LinkResponse> addLinkTracking(long chatId, String uri) {
        return restTemplate.exchange(
            scrapperUrl + "/links",
            HttpMethod.POST,
            new HttpEntity<>(new AddLinkRequest(uri, List.of(), List.of()), createHeaders(chatId)),
            LinkResponse.class);
    }

    public ResponseEntity<LinkResponse> removeLinkTracking(long chatId, String uri) {
        return restTemplate.exchange(
            scrapperUrl + "/links",
            HttpMethod.DELETE,
            new HttpEntity<>(new RemoveLinkRequest(uri), createHeaders(chatId)),
            LinkResponse.class
        );
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders createHeaders(long chatId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Tg-Chat-Id", String.valueOf(chatId));
        return headers;
    }
}
