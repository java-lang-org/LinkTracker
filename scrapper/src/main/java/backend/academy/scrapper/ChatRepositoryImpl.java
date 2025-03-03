package backend.academy.scrapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepositoryImpl implements ChatRepository {
    private final Set<Long> chatIds;

    public ChatRepositoryImpl() {
        this.chatIds = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public void registerChat(long chatId) {
        chatIds.add(chatId);
    }

    @Override
    public void deleteChat(long chatId) {
        chatIds.remove(chatId);
    }

    @Override
    public boolean exists(long chatId) {
        return chatIds.contains(chatId);
    }
}
