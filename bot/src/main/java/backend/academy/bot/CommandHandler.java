package backend.academy.bot;

import static backend.academy.bot.BotStateType.WAITING_TAG;
import static backend.academy.bot.BotStateType.WAITING_TRACKED_URL;
import static backend.academy.bot.BotStateType.WAITING_UNTRACKED_URL;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final ChatRepository chatRepository;
    private final ScrapperClient scrapperClient;
    private final CachedCommandService cachedCommandService;

    public String handle(long chatId, String receivedText) {
        return switch (chatRepository.getState(chatId).botStateType()) {
            case WAITING_TRACKED_URL -> handleTrackedUrl(chatId, receivedText);
            case WAITING_TAGS -> handleTags(chatId, receivedText);
            case WAITING_FILTER -> handleFilters(chatId, receivedText);
            case WAITING_UNTRACKED_URL -> handleUntrackedUrl(chatId, receivedText);
            case WAITING_TAG -> handleTag(chatId, receivedText);
            default -> handleCommand(chatId, receivedText);
        };
    }

    private String handleTrackedUrl(long chatId, String url) {
        chatRepository.setUrl(chatId, url);
        return "Enter tags separated by spaces otherwise '-':";
    }

    private String handleTags(long chatId, String tags) {
        if (tags.equals("-")) {
            chatRepository.setTags(chatId, List.of());
        } else {
            chatRepository.setTags(chatId, Arrays.stream(tags.split(" ")).toList());
        }
        return "Enter filters in format key:value separated by spaces otherwise '-':";
    }

    private String handleFilters(long chatId, String filters) {
        if (filters.equals("-")) {
            chatRepository.setFilters(chatId, List.of());
        } else {
            chatRepository.setFilters(chatId, Arrays.stream(filters.split(" ")).toList());
        }
        return handleTrack(chatId);
    }

    private String handleTrack(long chatId) {
        BotState botState = chatRepository.getStateAndSetDefault(chatId);
        return cachedCommandService.handleTrack(chatId, botState);
    }

    private String handleUntrackedUrl(long chatId, String url) {
        chatRepository.setDefault(chatId);
        return cachedCommandService.handleUntrackedUrl(chatId, url);
    }

    private String handleTag(long chatId, String tagName) {
        chatRepository.setDefault(chatId);
        return cachedCommandService.handleTag(chatId, tagName);
    }

    private String handleCommand(long chatId, String command) {
        return switch (command) {
            case "/start" -> handleStartCommand(chatId);
            case "/end" -> handleEndCommand(chatId);
            case "/set_immediate" -> handleSetImmediate(chatId);
            case "/set_digest" -> handleSetDigest(chatId);
            case "/track" -> handleTrackCommand(chatId);
            case "/untrack" -> handleUntrackCommand(chatId);
            case "/list" -> handleListCommand(chatId);
            case "/list_by_tag" -> handleListByTagCommand(chatId);
            case "/help" -> getHelpMessage();
            default -> """
                Unknown command.
                Use /help for more information.
                """;
        };
    }

    private String handleStartCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.registerChat(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return """
                Welcome!
                You have successfully registered.
                Use /help for more information.
                """;
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    private String handleEndCommand(long chatId) {
        return cachedCommandService.handleEndCommand(chatId);
    }

    private String handleSetImmediate(long chatId) {
        ResponseEntity<?> response = scrapperClient.setImmediate(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Immediate notification mode is set";
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    private String handleSetDigest(long chatId) {
        ResponseEntity<?> response = scrapperClient.setDigest(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Digest notification mode is set";
        } else {
            return ResponseEntityUtils.handleNotOkResponseEntity(response);
        }
    }

    private String handleTrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, WAITING_TRACKED_URL, "Enter link:");
    }

    private String handleUntrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, WAITING_UNTRACKED_URL, "Enter link:");
    }

    private String handleStateChangingCommand(long chatId, BotStateType state, String message) {
        chatRepository.setBotStateType(chatId, state);
        return message;
    }

    private String handleListCommand(long chatId) {
        return cachedCommandService.handleListCommand(chatId);
    }

    private String handleListByTagCommand(long chatId) {
        return handleStateChangingCommand(chatId, WAITING_TAG, "Enter tag:");
    }

    private String getHelpMessage() {
        return """
            /start - register chat
            /end - delete chat
            /set_immediate - set immediate
            /set_digest - set digest
            /track - track link
            /untrack - untrack link
            /list - show list of tracked links
            /list_by_tag - show list of tracked links by tag
            /help - list of commands
            """;
    }
}
