package backend.academy.bot;

import java.util.List;

public interface ChatRepository {
    void setDefault(long id);

    void setBotStateType(long id, BotStateType botStateType);

    void setUrl(long id, String url);

    void setTags(long id, List<String> tags);

    void setFilters(long id, List<String> filters);

    BotState getState(long id);

    default BotState getStateAndSetDefault(long id) {
        BotState botState = getState(id);
        setDefault(id);
        return botState;
    }
}
