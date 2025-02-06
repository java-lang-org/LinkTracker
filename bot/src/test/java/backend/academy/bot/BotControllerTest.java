package backend.academy.bot;

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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testUpdates_ValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        LinkUpdate validUpdate = new LinkUpdate(
            123L, "https://example.com", "Test update", List.of(456L)
        );

        // Act
        mockMvc.perform(
            post("/bot/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdate))
        ).andExpect(status().isOk());

        // Assert
        verify(botService, times(1)).sendMessage(123L, "Link 'https://example.com' was updated: 'Test update'");
    }

    @Test
    void updates_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidJson = """
            {
                "id": 123,
                "description": "Test update",
                "tgChatIds": [456]
            }
        """;

        // Act
        mockMvc.perform(post("/bot/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Failed to send message."));

        // Assert
        verifyNoInteractions(botService);
    }

    @Test
    void updates_ExceptionThrown_ShouldReturnBadRequest() throws Exception {
        // Arrange
        LinkUpdate update = new LinkUpdate(123L, "https://example.com", "Test update", List.of(456L));

        // Act
        doThrow(new RuntimeException("Simulated error")).when(botService).sendMessage(anyLong(), anyString());
        mockMvc.perform(post("/bot/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value("Failed to send message."));

        // Assert
        verify(botService, times(1)).sendMessage(
            123L,
            "Link 'https://example.com' was updated: 'Test update'"
        );
    }
}
