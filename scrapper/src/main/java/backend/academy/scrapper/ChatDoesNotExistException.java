package backend.academy.scrapper;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ChatDoesNotExistException extends ResponseStatusException {
    public ChatDoesNotExistException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
