package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ResponseEntityUtils {
    public static String handleNotOkResponseEntity(ResponseEntity<?> response) {
        if (response.getBody() instanceof ApiErrorResponse apiErrorResponse) {
            return handleApiErrorResponse(apiErrorResponse);
        } else {
            return handleUnexpectedResponseEntity(response);
        }
    }

    private static String handleApiErrorResponse(@NotNull ApiErrorResponse apiErrorResponse) {
        return apiErrorResponse.description();
    }

    private static String handleUnexpectedResponseEntity(ResponseEntity<?> response) {
        log.error(
                "Unexpected error while processing request: Status = {}, Body = {}",
                response.getStatusCode(),
                response.getBody());
        return "Oops, something went wrong!";
    }
}
