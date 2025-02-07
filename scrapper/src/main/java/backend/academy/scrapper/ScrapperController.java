package backend.academy.scrapper;

import backend.academy.dto.AddLinkRequest;
import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import backend.academy.dto.RemoveLinkRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/")
@Slf4j
public class ScrapperController {
    private final ChatRepository chatRepository;
    private final ObjectMapper objectMapper;

    public ScrapperController(ChatRepository chatRepository, ObjectMapper objectMapper) {
        this.chatRepository = chatRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping(path = "/tg-chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerChat(@RequestParam long id) {
        chatRepository.registerChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/tg-chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteChat(@RequestParam long id) {
        chatRepository.deleteChat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/links", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") long tgChatId) {
        List<LinkResponse> links = chatRepository.getLinks(tgChatId);
        int size = calculateJsonSize(links);
        return ResponseEntity.ok().body(new ListLinksResponse(links, size));
    }

    @PostMapping(
        path = "/links",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LinkResponse> addLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        LinkResponse linkResponse = chatRepository.addLink(tgChatId, addLinkRequest);
        return ResponseEntity.ok().body(linkResponse);
    }

    @DeleteMapping(
        path = "/links",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LinkResponse> removeLinkTracking(
        @RequestHeader("Tg-Chat-Id") long tgChatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest) {
        LinkResponse linkResponse = chatRepository.removeLink(tgChatId, removeLinkRequest);
        return ResponseEntity.ok().body(linkResponse);
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MethodArgumentNotValidException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestExceptions(Exception e) {
        log.error("Bad request: [{}] - {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ApiErrorResponse(
                "Incorrect request parameters",
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            )
        );
    }

    @ExceptionHandler({
        ChatDoesNotExistException.class,
        IncorrectRequestParametersException.class
    })
    public ResponseEntity<ApiErrorResponse> handleChatNotFound(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(
            new ApiErrorResponse(
                e.getReason(),
                String.valueOf(e.getStatusCode().value()),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            )
        );
    }

    private int calculateJsonSize(Object object) {
        try {
            byte[] jsonBytes = objectMapper.writeValueAsBytes(object);
            return jsonBytes.length;
        } catch (JsonProcessingException e) {
            log.error("Error calculating JSON size", e);
            return 0;
        }
    }
}
