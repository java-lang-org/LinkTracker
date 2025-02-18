package backend.academy.bot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.dto.LinkUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BotControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BotService botService;

    @InjectMocks
    private BotController botController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(botController).build();
    }

    @Test
    void updates_ValidRequest_ReturnsOk() throws Exception {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(0L, "http://example.com", "updated", List.of(1L, 12L, 123L));

        // Act & Assert
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkUpdate)))
                .andExpect(status().isOk());

        verify(botService, times(1)).updates(any());
    }

    @Test
    void updates_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidJson = "{\"id\": 123}"; // Отсутствует обязательное поле

        // Act & Assert
        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updates_ServiceThrowsException_ReturnsBadRequest() throws Exception {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(0L, "http://example.com", "updated", List.of(1L, 12L, 123L));
        doThrow(new RuntimeException("Service error")).when(botService).updates(linkUpdate);

        // Act & Assert
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(botService, times(1)).updates(any());
    }
}
