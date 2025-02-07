package backend.academy.scrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IncorrectRequestParametersException extends ResponseStatusException {
    public IncorrectRequestParametersException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
