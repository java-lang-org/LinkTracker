package backend.academy.bot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        // TODO:
    }
}
