package backend.academy.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepositoryImpl implements ChatRepository {
    private final Map<Long, BotState> users;

    public ChatRepositoryImpl() {
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public void registerChat(long id) {
        users.put(id, BotState.getInstance());
    }

    @Override
    public void deleteChat(long id) {
        users.remove(id);
    }

    @Override
    public void setDefault(long id) {
        if (users.containsKey(id)) {
            users.put(id, BotState.getInstance());
        }
    }

    @Override
    public void setBotStateType(long id, BotStateType botStateType) {
        users.computeIfPresent(
            id,
            (_, botState) -> {
                botState.botStateType(botStateType);
                return botState;
            }
        );
    }

    @Override
    public void setUrl(long id, String url) {
        users.computeIfPresent(
            id,
            (_, botState) -> {
                botState.botStateType(BotStateType.WAITING_TAGS);
                botState.url(url);
                return botState;
            }
        );
    }

    @Override
    public void setTags(long id, List<String> tags) {
        users.computeIfPresent(
            id,
            (_, botState) -> {
                botState.botStateType(BotStateType.WAITING_FILTER);
                botState.tags(new ArrayList<>(tags));
                return botState;
            }
        );
    }

    @Override
    public void setFilters(long id, List<String> filters) {
        users.computeIfPresent(
            id,
            (_, botState) -> {
                botState.botStateType(BotStateType.DEFAULT);
                botState.filters(new ArrayList<>(filters));
                return botState;
            }
        );
    }

    @Override
    public BotState getState(long id) {
        return users.getOrDefault(id, BotState.getInstance());
    }
}
