package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final ChatRepository chatRepository;
    private final ScrapperClient scrapperClient;

    public String handle(long chatId, String receivedText) {
        return switch (chatRepository.getState(chatId)) {
            case WAITING_FOR_TRACK_URL -> handleTrackUrl(chatId, receivedText);
            case WAITING_FOR_UNTRACK_URL -> handleUntrackUrl(chatId, receivedText);
            default -> handleCommand(chatId, receivedText);
        };
    }

    private String handleTrackUrl(long chatId, String url) {
        ResponseEntity<?> response = scrapperClient.addLinkTracking(chatId, url);
        chatRepository.setState(chatId, BotState.DEFAULT);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link '" + url + "' is tracked!";
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String handleUntrackUrl(long chatId, String url) {
        ResponseEntity<?> response = scrapperClient.removeLinkTracking(chatId, url);
        chatRepository.setState(chatId, BotState.DEFAULT);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link '" + url + "' isn't tracked!";
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
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
            default -> """
                Unknown command.
                Use /help for more information.
                """;
        };
    }

    private String handleStartCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.registerChat(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            chatRepository.registerChat(chatId);
            return """
                Welcome!
                You have successfully registered.
                Use /help for more information.
                """;
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String handleEndCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.deleteChat(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            chatRepository.deleteChat(chatId);
            return "Bye! You have successfully unregistered.";
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getBody() == HttpStatus.NOT_FOUND) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String handleTrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, BotState.WAITING_FOR_TRACK_URL);
    }

    private String handleUntrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, BotState.WAITING_FOR_UNTRACK_URL);
    }

    private String handleStateChangingCommand(long chatId, BotState state) {
        chatRepository.setState(chatId, state);
        return "Enter link:";
    }

    private String handleListCommand(long chatId) {
        ResponseEntity<?> response = scrapperClient.getLinks(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            ListLinksResponse listLinksResponse = (ListLinksResponse) response.getBody();

            if (listLinksResponse.links().isEmpty()) {
                return "No tracked links.";
            }

            return listLinksResponse.links().stream()
                .map(LinkResponse::url)
                .collect(Collectors.joining("\n"));
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String getHelpMessage() {
        return """
            /start - register chat
            /end - delete chat
            /track - track a link
            /untrack - untrack a link
            /list - show list of tracked links
            /help - list of commands
            """;
    }

    private String handleUnexpectedResponse(ResponseEntity<?> response) {
        log.error(
            "Unexpected error while processing request: Status = {}, Body = {}",
            response.getStatusCode(),
            response.getBody()
        );
        return "Oops, something went wrong!";
    }
}
