package backend.academy.bot;

public interface UserRepository {
    boolean isUserRegistered(long id);

    void registerUser(long id);

    BotState getState(long id);

    void setState(long id, BotState botState);
}
