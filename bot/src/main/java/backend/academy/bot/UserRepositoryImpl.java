package backend.academy.bot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, BotState> users;

    public UserRepositoryImpl() {
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isUserRegistered(long id) {
        return users.containsKey(id);
    }

    @Override
    public void registerUser(long id) {
        users.put(id, BotState.DEFAULT);
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
