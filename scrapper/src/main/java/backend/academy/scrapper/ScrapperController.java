package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.ChatService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class ScrapperController {
    private static final int STACK_TRACE_MAX_SIZE = 10;

    private final ChatService chatService;

    @PostMapping(path = "/tg-chat/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerChat(@PathVariable long id) {
        chatService.registerChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/tg-chat/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteChat(@PathVariable long id) {
        chatService.deleteChat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") long tgChatId) {
        List<LinkResponse> links = chatService.getLinks(tgChatId);
        return ResponseEntity.ok().body(new ListLinksResponse(links, links.size()));
    }

    @GetMapping(path = "/links/{tag-name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListLinksResponse> getLinksByTag(
            @RequestHeader("Tg-Chat-Id") long tgChatId, @PathVariable("tag-name") String tagName) {
        List<LinkResponse> links = chatService.getLinksByTag(tgChatId, tagName);
        return ResponseEntity.ok().body(new ListLinksResponse(links, links.size()));
    }

    @PostMapping(
            path = "/links",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkResponse> addLinkTracking(
            @RequestHeader("Tg-Chat-Id") long tgChatId, @Valid @RequestBody AddLinkRequest addLinkRequest) {
        Link link = Link.getInstance(addLinkRequest.url());
        LinkResponse linkResponse =
                chatService.addLink(tgChatId, link, addLinkRequest.tags(), addLinkRequest.filters());
        return ResponseEntity.ok().body(linkResponse);
    }

    @DeleteMapping(
            path = "/links",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkResponse> removeLinkTracking(
            @RequestHeader("Tg-Chat-Id") long tgChatId, @Valid @RequestBody RemoveLinkRequest removeLinkRequest) {
        LinkResponse linkResponse = chatService.removeLink(tgChatId, removeLinkRequest.uri());
        return ResponseEntity.ok().body(linkResponse);
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception e) {
        log.error("Bad request: [{}] - {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        "Incorrect request parameters",
                        String.valueOf(HttpStatus.BAD_REQUEST.value()),
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()));
    }

    @ExceptionHandler({ChatException.class, InvalidRequestException.class})
    public ResponseEntity<ApiErrorResponse> handleInternalExceptions(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(new ApiErrorResponse(
                        e.getReason(),
                        String.valueOf(e.getStatusCode().value()),
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        Arrays.stream(e.getStackTrace())
                                .limit(STACK_TRACE_MAX_SIZE)
                                .map(StackTraceElement::toString)
                                .toList()));
    }
}
