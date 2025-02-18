package backend.academy.scrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ChatLinkRepositoryImpl implements ChatLinkRepository {
    private final Map<Long, Set<Link>> chatId2Links = new ConcurrentHashMap<>();
    private final Map<Link, Set<Long>> link2ChatIds = new ConcurrentHashMap<>();

    @Override
    public List<Link> getLinks(long chatId) {
        return new ArrayList<>(chatId2Links.getOrDefault(chatId, Set.of()));
    }

    @Override
    public boolean addLink(long chatId, Link link) {
        Set<Link> links = chatId2Links.computeIfAbsent(chatId, k -> new HashSet<>());
        if (links.contains(link)) {
            return false;
        }

        links.add(link);
        link2ChatIds.computeIfAbsent(link, k -> new HashSet<>()).add(chatId);

        return true;
    }

    @Override
    public Optional<Link> removeLink(long chatId, String url) {
        if (!chatId2Links.containsKey(chatId)) {
            return Optional.empty();
        }

        Optional<Link> linkToRemove = chatId2Links.get(chatId).stream()
                .filter(link -> link.url().equals(url))
                .findFirst();

        if (linkToRemove.isEmpty()) {
            return linkToRemove;
        }

        chatId2Links.get(chatId).remove(linkToRemove.orElseThrow());
        removeChatIdForLink(chatId, linkToRemove.orElseThrow());

        return linkToRemove;
    }

    private void removeChatIdForLink(long chatId, Link link) {
        Set<Long> chatIds = link2ChatIds.get(link);
        chatIds.remove(chatId);
        if (chatIds.isEmpty()) {
            link2ChatIds.remove(link);
        }
    }

    @Override
    public List<ChatLink> getLink2ChatIds() {
        return link2ChatIds.entrySet().stream()
                .map(entry -> new ChatLink(entry.getKey(), new ArrayList<>(entry.getValue())))
                .toList();
    }
}
