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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BotService {
    private static final List<BotCommand> COMMANDS = List.of(
            new BotCommand("/start", "Register chat"),
            new BotCommand("/end", "Delete chat"),
            new BotCommand("/track", "Track a link"),
            new BotCommand("/untrack", "Untrack a link"),
            new BotCommand("/list", "Show list of tracked links"),
            new BotCommand("/help", "List of commands"));

    private static final int TIMEOUT_IN_SECONDS = 2;
    private static final int TELEGRAM_MESSAGE_LIMIT = 4096;

    private final TelegramBot bot;
    private final ExecutorService executorService;
    private final CommandHandler commandHandler;

    @Autowired
    public BotService(TelegramBot bot, ExecutorService executorService, CommandHandler commandHandler) {
        this.bot = bot;
        this.executorService = executorService;
        this.commandHandler = commandHandler;
    }

    @PostConstruct
    public void startBot() {
        try {
            registerCommands();
            bot.setUpdatesListener(updates -> {
                for (Update update : updates) {
                    executorService.submit(() -> processUpdate(update));
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });
        } catch (Exception e) {
            log.error("Failed to start bot", e);
        }
    }

    public void registerCommands() {
        bot.execute(new SetMyCommands(COMMANDS.toArray(new BotCommand[0])));
    }

    @PreDestroy
    public void stopBot() {
        bot.removeGetUpdatesListener();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            log.info("Bot service has been stopped.");
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Bot service termination was interrupted.");
        }
    }

    public void updates(LinkUpdate linkUpdate) {
        String message = "Link '" + linkUpdate.url() + "' was updated: " + linkUpdate.description();
        for (long chatId : linkUpdate.tgChatIds()) {
            sendMessage(chatId, message);
        }
    }

    private void processUpdate(Update update) {
        try {
            if (update.message() == null) {
                return;
            }

            if (update.message().text() == null) {
                log.info(
                        "Received a non-text message from chat {}. Ignoring.",
                        update.message().chat().id());
                return;
            }

            long chatId = update.message().chat().id();
            String receivedText = update.message().text().trim();
            log.info("Processing message from {}: {}", chatId, receivedText);

            String response = commandHandler.handle(chatId, receivedText);
            sendMessage(chatId, response);
        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
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
