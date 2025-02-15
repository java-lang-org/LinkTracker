package backend.academy.bot;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotState {
    private BotStateType botStateType;
    private String url;
    private List<String> tags;
    private List<String> filters;

    private BotState(BotStateType botStateType, String url, List<String> tags, List<String> filters) {
        this.botStateType = botStateType;
        this.url = url;
        this.tags = tags;
        this.filters = filters;
    }

    public static BotState getInstance() {
        return new BotState(BotStateType.DEFAULT, "", List.of(), List.of());
    }
}
