package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BotService {
    private final TelegramBot bot;
    private final UserRepository userRepository;
    private final CommandHandler commandHandler;
    private final ExecutorService executorService;
    private final int timeoutInSeconds;

    @Autowired
    public BotService(BotConfig botConfig, UserRepository userRepository) {
        this.bot = new TelegramBot(botConfig.telegramToken());
        this.userRepository = userRepository;
        this.commandHandler = new CommandHandler(this, userRepository);
        this.executorService = Executors.newFixedThreadPool(botConfig.nThreads());
        this.timeoutInSeconds = botConfig.timeoutInSeconds();
    }

    /**
     * This is constructor special for tests.
     */
    protected BotService(BotConfig botConfig, UserRepository userRepository, CommandHandler commandHandler) {
        this.bot = new TelegramBot(botConfig.telegramToken());
        this.userRepository = userRepository;
        this.commandHandler = commandHandler;
        this.executorService = Executors.newFixedThreadPool(botConfig.nThreads());
        this.timeoutInSeconds = botConfig.timeoutInSeconds();
    }

    @PostConstruct
    public void startBot() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                executorService.submit(() -> processUpdate(update));
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @PreDestroy
    public void stopBot() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    protected void processUpdate(Update update) {
        try {
            if (update.message() == null || update.message().text() == null) {
                log.warn("Received an update without a message: {}", update);
                return;
            }

            long chatId = update.message().chat().id();
            String receivedText = update.message().text().trim();
            log.info("Processing message from {}: {}", chatId, receivedText);

            switch (userRepository.getState(chatId)) {
                case WAITING_FOR_TRACK_URL -> handleTrackUrl(chatId, receivedText);
                case WAITING_FOR_UNTRACK_URL -> handleUntrackUrl(chatId, receivedText);
                default -> commandHandler.handleCommand(chatId, receivedText);
            }
        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
        }
    }

    private void handleTrackUrl(long chatId, String url) {
        if (!isValidUrl(url)) {
            sendMessage(chatId, "Invalid URL. Please enter a valid link.");
            return;
        }

        userRepository.setState(chatId, BotState.DEFAULT);
        sendMessage(chatId, "Link '" + url + "' added for tracking!");
    }

    private void handleUntrackUrl(long chatId, String url) {
        if (!isValidUrl(url)) {
            sendMessage(chatId, "Invalid URL. Please enter a valid link.");
            return;
        }

        userRepository.setState(chatId, BotState.DEFAULT);
        sendMessage(chatId, "Link '" + url + "' untracked!");
    }

    private boolean isValidUrl(String url) {
        try {
            URI.create(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void sendMessage(long chatId, String text) {
        SendMessage request = new SendMessage(chatId, text);
        try {
            SendResponse response = bot.execute(request);
            if (!response.isOk()) {
                log.error("Error sending message: {}", response.description());
            }
        } catch (Exception e) {
            log.error("Exception while sending message", e);
        }
    }
}
