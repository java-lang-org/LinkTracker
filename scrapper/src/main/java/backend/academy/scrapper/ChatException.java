package backend.academy.scrapper;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ChatException extends ResponseStatusException {
    public ChatException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
