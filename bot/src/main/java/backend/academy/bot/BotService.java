package backend.academy.bot;

import backend.academy.dto.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotService {
    private static final int TIMEOUT_IN_SECONDS = 2;
    private static final int TELEGRAM_MESSAGE_LIMIT = 4096;

    private final TelegramBot bot;
    private final ExecutorService executorService;
    private final CommandHandler commandHandler;

    @PostConstruct
    public void startBot() {
        try {
            log.info("Starting bot service at {}", Utils.getCurrentTimestamp());
            registerCommands();
            bot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    executorService.submit(() -> processUpdate(update));
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });
            log.info("Bot service started successfully");
        } catch (Exception e) {
            log.error("Failed to start bot at {}", Utils.getCurrentTimestamp(), e);
        }
    }

    @PreDestroy
    public void stopBot() {
        log.info("Stopping bot service at {}", Utils.getCurrentTimestamp());
        bot.removeGetUpdatesListener();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            log.info("Bot service has been stopped successfully.");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Bot service termination was interrupted at {}", Utils.getCurrentTimestamp());
        }
    }

    public void updates(LinkUpdate linkUpdate) {
        if (linkUpdate == null || Utils.isNullOrBlank(linkUpdate.url())) {
            log.warn("Received invalid link update at {}", Utils.getCurrentTimestamp());
            return;
        }
        
        String message = "Link '" + linkUpdate.url() + "' was updated: " + linkUpdate.description();
        log.info("Sending update notification for URL: {} at {}", linkUpdate.url(), Utils.getCurrentTimestamp());
        
        for (long chatId : linkUpdate.tgChatIds()) {
            sendMessage(chatId, message);
        }
    }

    private void registerCommands() {
        BotCommand[] botCommands = Arrays.stream(BotCommandEnum.values())
                .map(BotCommandEnum::toBotCommand)
                .toArray(BotCommand[]::new);

        bot.execute(new SetMyCommands(botCommands));
    }

    private void processUpdate(Update update) {
        try {
            if (update.message() == null) {
                return;
            }

            if (update.message().text() == null) {
                log.info(
                        "Received a non-text message from chat {} at {}. Ignoring.",
                        update.message().chat().id(), Utils.getCurrentTimestamp());
                return;
            }

            long chatId = update.message().chat().id();
            String receivedText = Utils.sanitizeInput(update.message().text());
            
            if (Utils.isNullOrBlank(receivedText)) {
                log.warn("Received empty or whitespace-only message from chat {} at {}", 
                        chatId, Utils.getCurrentTimestamp());
                return;
            }
            
            log.info("Processing message from {} at {}: {}", chatId, Utils.getCurrentTimestamp(), receivedText);

            String response = commandHandler.handle(chatId, receivedText);
            sendMessage(chatId, response);
        } catch (Exception e) {
            log.error("Error processing update at {}: {}", Utils.getCurrentTimestamp(), update, e);
        }
    }

    private void sendMessage(long chatId, String message) {
        for (String part : splitMessage(message)) {
            SendMessage request = new SendMessage(chatId, part);
            try {
                SendResponse response = bot.execute(request);
                if (!response.isOk()) {
                    log.warn("Warning sending message: {}", response.description());
                }
            } catch (Exception e) {
                log.error("Error while sending message", e);
            }
        }
    }

    private List<String> splitMessage(String message) {
        List<String> parts = new LinkedList<>();
        while (message.length() > TELEGRAM_MESSAGE_LIMIT) {
            int splitIndex = calculateSplitIndex(message);
            parts.add(message.substring(0, splitIndex).trim());
            message = message.substring(splitIndex).trim();
        }
        parts.add(message);
        return parts;
    }

    private int calculateSplitIndex(String message) {
        int splitIndex = message.lastIndexOf('\n', TELEGRAM_MESSAGE_LIMIT);
        if (splitIndex != -1) {
            return splitIndex;
        }

        splitIndex = message.lastIndexOf(' ', TELEGRAM_MESSAGE_LIMIT);
        if (splitIndex != -1) {
            return splitIndex;
        }

        return TELEGRAM_MESSAGE_LIMIT;
    }
}
