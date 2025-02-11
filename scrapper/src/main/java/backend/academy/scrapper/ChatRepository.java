package backend.academy.scrapper;

public interface ChatRepository {
    void registerChat(long chatId);

    void deleteChat(long chatId);

    boolean exists(long chatId);
}
