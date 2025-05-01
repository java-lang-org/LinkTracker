package backend.academy.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class ScrapperClientTest {
    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private RestClient restClient;

    @InjectMocks
    private ScrapperClient scrapperClient;

    private final long chatId = 1L;

    @BeforeEach
    void setUp() {
        reset(restClient);
    }

    @Test
    void testNetworkExceptionHandling() {
        // Arrange
        RestClientException exception = new RestClientException("Network Error");

        when(retryTemplate.execute(any())).thenThrow(exception);

        // Act
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
