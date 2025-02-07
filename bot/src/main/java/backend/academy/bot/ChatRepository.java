package backend.academy.bot;

public interface ChatRepository {
    boolean isChatRegistered(long id);

    void registerChat(long id);

    void deleteChat(long id);

    BotState getState(long id);

    void setState(long id, BotState botState);
}
