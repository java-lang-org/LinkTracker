package backend.academy.bot;

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
        users.put(id, BotState.DEFAULT);
    }

    @Override
    public void deleteChat(long id) {
        users.remove(id);
    }

    @Override
    public BotState getState(long id) {
        return users.getOrDefault(id, BotState.DEFAULT);
    }

    @Override
    public void setState(long id, BotState botState) {
        users.put(id, botState);
    }
}
