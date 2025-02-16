package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScrapperClientTest {
    @Mock
    private RestClient restClient;

    @InjectMocks
    private ScrapperClient scrapperClient;

    private final long chatId = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        reset(restClient);
    }

    @Test
    void testBadRequestErrorHandling() throws JsonProcessingException {
        // Arrange
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "description",
            "400",
            "exception-name",
            "exception-message",
            List.of()
        );
        HttpClientErrorException exception = getHttpException(
            (one, two, three) -> new HttpClientErrorException(
                one,
                two,
                objectMapper.writeValueAsBytes(three),
                StandardCharsets.UTF_8
            ),
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            apiErrorResponse
        );

        when(restClient.get()).thenThrow(exception);

        // Act
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ApiErrorResponse.class, response.getBody());

        ApiErrorResponse responseBody = (ApiErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("description", responseBody.description());
        assertEquals("exception-message", responseBody.exceptionMessage());
    }


    @Test
    void testNotFoundErrorHandling() throws JsonProcessingException {
        // Arrange
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "description",
            "404",
            "exception-name",
            "exception-message",
            List.of()
        );
        HttpClientErrorException exception = getHttpException(
            (one, two, three) -> new HttpClientErrorException(
                one,
                two,
                objectMapper.writeValueAsBytes(three),
                StandardCharsets.UTF_8
            ),
            HttpStatus.NOT_FOUND,
            "Not Found",
            apiErrorResponse
        );

        when(restClient.get()).thenThrow(exception);

        // Act
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ApiErrorResponse.class, response.getBody());

        ApiErrorResponse responseBody = (ApiErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("description", responseBody.description());
        assertEquals("404", responseBody.code());
        assertEquals("exception-name", responseBody.exceptionName());
        assertEquals("exception-message", responseBody.exceptionMessage());
    }


    @Test
    void testInternalServerErrorHandling() throws JsonProcessingException {
        // Arrange
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
            "description",
            "500",
            "exception-name",
            "exception-message",
            List.of()
        );
        HttpServerErrorException exception = getHttpException(
            (one, two, three) -> new HttpServerErrorException(
                one,
                two,
                objectMapper.writeValueAsBytes(three),
                StandardCharsets.UTF_8
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            apiErrorResponse
        );

        when(restClient.get()).thenThrow(exception);

        // Act
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(ApiErrorResponse.class, response.getBody());

        ApiErrorResponse responseBody = (ApiErrorResponse) response.getBody();
        assertNotNull(responseBody);
        assertEquals("description", responseBody.description());
        assertEquals("500", responseBody.code());
        assertEquals("exception-name", responseBody.exceptionName());
        assertEquals("exception-message", responseBody.exceptionMessage());
    }


    @Test
    void testNetworkExceptionHandling() {
        // Arrange
        RestClientException exception = new RestClientException("Network Error");

        when(restClient.get()).thenThrow(exception);

        // Act
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    private <T extends RestClientResponseException> T getHttpException(
        QuadFunction<HttpStatus, String, Object, T> constructor,
        HttpStatus httpStatus,
        String statusText,
        Object body
    ) throws JsonProcessingException {
        T exception = constructor.apply(httpStatus, statusText, body);

        exception.setBodyConvertFunction(
            resolvableType -> {
                try {
                    return objectMapper.readValue(
                        exception.getResponseBodyAsByteArray(),
                        objectMapper.constructType(resolvableType.getType())
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Failed to parse response body", e);
                }
            }
        );

        return exception;
    }

    @FunctionalInterface
    interface QuadFunction<One, Two, Three, Four> {
        Four apply(One one, Two two, Three three) throws JsonProcessingException;
    }
}
