package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandHandler {
    private final ChatRepository chatRepository;
    private final ScrapperService scrapperService;

    public String handle(long chatId, String receivedText) {
        return switch (chatRepository.getState(chatId)) {
            case WAITING_FOR_TRACK_URL -> handleTrackUrl(chatId, receivedText);
            case WAITING_FOR_UNTRACK_URL -> handleUntrackUrl(chatId, receivedText);
            default -> handleCommand(chatId, receivedText);
        };
    }

    private String handleTrackUrl(long chatId, String url) {
        return handleLinkTracking(
            chatId,
            url,
            uri -> scrapperService.addLinkTracking(chatId, uri),
            "Link '%s' is tracked!"
        );
    }

    private String handleUntrackUrl(long chatId, String url) {
        return handleLinkTracking(
            chatId,
            url,
            uri -> scrapperService.removeLinkTracking(chatId, uri),
            "Link '%s' isn't tracked!"
        );

    }

    private String handleLinkTracking(
        long chatId,
        String url,
        Function<String, ResponseEntity<LinkResponse>> action,
        String successMessage
    ) {
        if (!isValidUrl(url)) {
            return "Invalid URL. Please enter a valid link.";
        }

        ResponseEntity<LinkResponse> response = action.apply(url);
        if (response.getStatusCode().is2xxSuccessful()) {
            chatRepository.setState(chatId, BotState.DEFAULT);
            return successMessage.formatted(url);
        } else {
            return "Something went wrong: " + response.getBody();
        }
    }

    private boolean isValidUrl(String url) {
        try {
            URI.create(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String handleCommand(long chatId, String command) {
        return switch (command) {
            case "/start" -> handleStartCommand(chatId);
            case "/end" -> handleEndCommand(chatId);
            case "/track" -> handleTrackCommand(chatId);
            case "/untrack" -> handleUntrackCommand(chatId);
            case "/list" -> handleListCommand(chatId);
            case "/help" -> getHelpMessage();
            default -> "Unknown command. Type /help for a list of available commands.";
        };
    }

    private String handleStartCommand(long chatId) {
        if (chatRepository.isChatRegistered(chatId)) {
            return "You are already registered. Type /help for a list of commands.";
        } else {
            ResponseEntity<ApiErrorResponse> response = scrapperService.registerChat(chatId);
            if (response.getStatusCode().is2xxSuccessful()) {
                chatRepository.registerChat(chatId);
                return "Welcome! You have successfully registered. Type /help for a list of commands.";
            } else {
                return "Something went wrong: " + response.getBody();
            }
        }
    }

    private String handleEndCommand(long chatId) {
        if (chatRepository.isChatRegistered(chatId)) {
            ResponseEntity<ApiErrorResponse> response = scrapperService.deleteChat(chatId);
            if (response.getStatusCode().is2xxSuccessful()) {
                chatRepository.deleteChat(chatId);
                return "Bye! You have successfully unregistered.";
            } else {
                return "Something went wrong: " + response.getBody();
            }
        } else {
            return "You are not registered yet.";
        }
    }

    private String handleTrackCommand(long chatId) {
        return handleStateChangingCommand(
            chatId,
            BotState.WAITING_FOR_TRACK_URL,
            "Enter the link you want to track:"
        );
    }

    private String handleUntrackCommand(long chatId) {
        return handleStateChangingCommand(
            chatId,
            BotState.WAITING_FOR_UNTRACK_URL,
            "Enter the link you want to untrack:"
        );
    }

    private String handleStateChangingCommand(long chatId, BotState state, String message) {
        if (!isChatRegistered(chatId)) {
            return "To use this command you must be registered.";
        }
        chatRepository.setState(chatId, state);
        return message;
    }

    private String handleListCommand(long chatId) {
        if (!isChatRegistered(chatId)) {
            return "To use this command you must be registered.";
        }

        ResponseEntity<ListLinksResponse> response = scrapperService.getLinks(chatId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            return "Something went wrong: " + response.getBody();
        }

        ListLinksResponse listLinksResponse = response.getBody();
        if (listLinksResponse.links().isEmpty()) {
            return "No tracked links found.";
        }

        return listLinksResponse.links().stream()
            .map(LinkResponse::toString)
            .collect(Collectors.joining("\n"));
    }

    private boolean isChatRegistered(long chatId) {
        return chatRepository.isChatRegistered(chatId);
    }

    private String getHelpMessage() {
        return """
            Available commands:
            /start - register chat
            /end - delete chat
            /track - track a link
            /untrack - untrack a link
            /list - show list of tracked links
            /help - list of commands
            """;
    }
}
