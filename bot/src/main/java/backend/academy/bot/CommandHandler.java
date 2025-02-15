package backend.academy.bot;

import backend.academy.dto.ApiErrorResponse;
import backend.academy.dto.LinkResponse;
import backend.academy.dto.ListLinksResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import static backend.academy.bot.BotStateType.WAITING_TRACKED_URL;
import static backend.academy.bot.BotStateType.WAITING_UNTRACKED_URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {
    private final ChatRepository chatRepository;
    private final ScrapperClient scrapperClient;

    public String handle(long chatId, String receivedText) {
        return switch (chatRepository.getState(chatId).botStateType()) {
            case WAITING_TRACKED_URL -> handleTrackedUrl(chatId, receivedText);
            case WAITING_TAGS -> handleTags(chatId, receivedText);
            case WAITING_FILTER -> handleFilters(chatId, receivedText);
            case WAITING_UNTRACKED_URL -> handleUntrackedUrl(chatId, receivedText);
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
        BotState botState = chatRepository.getState(chatId);
        ResponseEntity<?> response = scrapperClient.addLinkTracking(chatId, botState);
        chatRepository.setDefault(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link is tracked!";
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String handleUntrackedUrl(long chatId, String url) {
        ResponseEntity<?> response = scrapperClient.removeLinkTracking(chatId, url);
        chatRepository.setDefault(chatId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Link is untracked!";
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
            return "Bye! You have successfully unregistered.";
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST ||
            response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ((ApiErrorResponse) response.getBody()).description();
        } else {
            return handleUnexpectedResponse(response);
        }
    }

    private String handleTrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, WAITING_TRACKED_URL);
    }

    private String handleUntrackCommand(long chatId) {
        return handleStateChangingCommand(chatId, WAITING_UNTRACKED_URL);
    }

    private String handleStateChangingCommand(long chatId, BotStateType state) {
        chatRepository.setBotStateType(chatId, state);
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
                .map(LinkResponse::toString)
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
