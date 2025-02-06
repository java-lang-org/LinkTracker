package backend.academy.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final BotService botService;
    private final UserRepository userRepository;

    public void handleCommand(long chatId, String command) {
        switch (command) {
            case "/start" -> handleStartCommand(chatId);
            case "/track" -> handleTrackCommand(chatId);
            case "/untrack" -> handleUntrackCommand(chatId);
            case "/list" -> handleListCommand(chatId);
            case "/help" -> botService.sendMessage(chatId, getHelpMessage());
            default -> botService.sendMessage(chatId, "Unknown command. Type /help for a list of available commands.");
        }
    }

    private void handleStartCommand(long chatId) {
        if (userRepository.isUserRegistered(chatId)) {
            botService.sendMessage(chatId, "You are already registered. Type /help for a list of commands.");
        } else {
            userRepository.registerUser(chatId);
            botService.sendMessage(chatId, "Welcome! You have successfully registered. Type /help for a list of commands.");
        }
    }

    private void handleTrackCommand(long chatId) {
        if (!isUserRegistered(chatId)) {
            return;
        }

        userRepository.setState(chatId, BotState.WAITING_FOR_TRACK_URL);
        botService.sendMessage(chatId, "Enter the link you want to track:");
    }

    private void handleUntrackCommand(long chatId) {
        if (!isUserRegistered(chatId)) {
            return;
        }

        userRepository.setState(chatId, BotState.WAITING_FOR_UNTRACK_URL);
        botService.sendMessage(chatId, "Enter the link you want to untrack:");
    }

    private void handleListCommand(long chatId) {
        if (!isUserRegistered(chatId)) {
            return;
        }

        botService.sendMessage(chatId, "List of tracked links: (stub)");
    }

    private boolean isUserRegistered(long chatId) {
        if (userRepository.isUserRegistered(chatId)) {
            return true;
        }
        botService.sendMessage(chatId, "To use this command you must be registered.");
        return false;
    }

    private String getHelpMessage() {
        return """
            Available commands:
            /start - user registration
            /track - track a link
            /untrack - untrack a link
            /list - show list of tracked links
            /help - list of commands
            """;
    }
}
