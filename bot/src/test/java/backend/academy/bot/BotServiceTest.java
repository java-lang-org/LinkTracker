package backend.academy.bot;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BotServiceTest {
    @Mock
    private TelegramBot telegramBot;

    @Mock
    private ExecutorService executorService;

    @Mock
    private CommandHandler commandHandler;

    @InjectMocks
    private BotService botService;

    @Test
    void startBot_shouldSetUpdatesListener() {
        // Arrange

        // Act
        botService.startBot();

        // Assert
        verify(telegramBot, times(1)).setUpdatesListener(any(UpdatesListener.class));
    }

    @Test
    void stopBot_shouldShutdownExecutorService() throws InterruptedException {
        // Arrange
        when(executorService.awaitTermination(anyLong(), any())).thenReturn(true);

        // Act
        botService.stopBot();

        // Assert
        verify(executorService, times(1)).shutdown();
        verify(executorService, times(1)).awaitTermination(anyLong(), any());
    }

    @Test
    void updates_shouldUpdate() {
        // Arrange
        LinkUpdate linkUpdate = new LinkUpdate(0L, "http://example.com", "updated", List.of(1L, 12L));

        // Act
        botService.updates(linkUpdate);

        // Assert
        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(captor.capture());

        List<SendMessage> capturedMessages = captor.getAllValues();
        assertThat(capturedMessages).hasSize(2);
        assertThat(capturedMessages.get(0).getParameters().get("chat_id")).isEqualTo(1L);
        assertThat(capturedMessages.get(1).getParameters().get("chat_id")).isEqualTo(12L);
        assertThat(capturedMessages.get(0).getParameters().get("text"))
                .isEqualTo("Link 'http://example.com' was updated: updated");
    }
}
