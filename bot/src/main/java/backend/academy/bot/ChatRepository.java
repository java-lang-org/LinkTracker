package backend.academy.bot;

public interface ChatRepository {
    void registerChat(long id);

    void deleteChat(long id);

    BotState getState(long id);

    void setState(long id, BotState botState);
}
