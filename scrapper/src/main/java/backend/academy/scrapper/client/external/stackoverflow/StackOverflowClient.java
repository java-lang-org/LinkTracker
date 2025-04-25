package backend.academy.scrapper.client.external.stackoverflow;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.client.external.ExternalClient;
import backend.academy.scrapper.config.StackOverflowConfig;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StackOverflowClient extends ExternalClient {
    public StackOverflowClient(
            StackOverflowConfig stackOverflowConfig, @Qualifier("stackOverflowRestClient") RestClient restClient) {
        super(stackOverflowConfig.url(), restClient);
    }

    @Override
    public List<String> getRecentEvents(Link link) {
        String questionId = extractQuestionId(link.uri().getPath());

        StackOverflowResponse questionResponse = fetchQuestion(questionId);
        if (questionResponse == null || questionResponse.items().isEmpty()) {
            return List.of();
        }

        StackOverflowQuestion question = questionResponse.items().getFirst();

        List<StackOverflowEvent> answers = fetchAnswers(questionId);
        List<StackOverflowEvent> comments = fetchComments(questionId);

        return Stream.concat(answers.stream(), comments.stream())
                .filter(event -> link.lastUpdate().isBefore(event.creationDate()))
                .peek(event -> {
                    if (link.lastUpdate().isBefore(event.creationDate())) {
                        link.lastUpdate(event.creationDate());
                    }
                })
                .map(event -> formatEventMessage(question, event))
                .toList();
    }

    private String extractQuestionId(String path) {
        return path.split("/")[2];
    }

    private StackOverflowResponse fetchQuestion(String questionId) {
        String uri = UriComponentsBuilder.fromUriString(baseUrl())
                .path("/questions/{id}")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .buildAndExpand(questionId)
                .toUriString();

        return restClient().get().uri(uri).retrieve().body(StackOverflowResponse.class);
    }

    private List<StackOverflowEvent> fetchAnswers(String questionId) {
        String url = UriComponentsBuilder.fromUriString(baseUrl())
                .path("/questions/{id}/answers")
                .queryParam("order", "desc")
                .queryParam("sort", "creation")
                .queryParam("site", "stackoverflow")
                .queryParam("filter", "withbody")
                .buildAndExpand(questionId)
                .toUriString();

        StackOverflowEventResponse response =
                restClient().get().uri(url).retrieve().body(StackOverflowEventResponse.class);
        return response == null ? List.of() : response.items();
    }

    private List<StackOverflowEvent> fetchComments(String questionId) {
        String url = UriComponentsBuilder.fromUriString(baseUrl())
                .path("/questions/{id}/comments")
                .queryParam("order", "desc")
                .queryParam("sort", "creation")
                .queryParam("site", "stackoverflow")
                .queryParam("filter", "withbody")
                .buildAndExpand(questionId)
                .toUriString();

        StackOverflowEventResponse response =
                restClient().get().uri(url).retrieve().body(StackOverflowEventResponse.class);
        return response == null ? List.of() : response.items();
    }

    private String formatEventMessage(StackOverflowQuestion question, StackOverflowEvent event) {
        return String.format(
                "**%s** (by %s)%n%s%n%s",
                question.title(), event.ownerName(), event.creationDate(), truncate(event.body()));
    }
}
