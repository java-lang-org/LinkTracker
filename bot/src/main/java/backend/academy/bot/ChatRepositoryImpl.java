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
    public void setDefault(long id) {
        users.remove(id);
    }

    @Override
    public void setBotStateType(long id, BotStateType botStateType) {
        users.computeIfAbsent(id, _ -> BotState.getInstance())
            .botStateType(botStateType);
    }

    @Override
    public void setUrl(long id, String url) {
        users.computeIfAbsent(id, _ -> BotState.getInstance())
            .url(url)
            .botStateType(BotStateType.WAITING_TAGS);
    }

    @Override
    public void setTags(long id, List<String> tags) {
        users.computeIfAbsent(id, _ -> BotState.getInstance())
            .tags(new ArrayList<>(tags))
            .botStateType(BotStateType.WAITING_FILTER);
    }

    @Override
    public void setFilters(long id, List<String> filters) {
        users.computeIfAbsent(id, _ -> BotState.getInstance())
            .filters(new ArrayList<>(filters))
            .botStateType(BotStateType.DEFAULT);
    }

    @Override
    public BotState getState(long id) {
        return users.getOrDefault(id, BotState.getInstance());
    }
}
