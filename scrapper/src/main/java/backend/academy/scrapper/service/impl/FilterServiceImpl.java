package backend.academy.scrapper.service.impl;

import backend.academy.scrapper.Link;
import backend.academy.scrapper.entity.FilterEntity;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.service.FilterService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FilterServiceImpl implements FilterService {
    FilterRepository filterRepository;

    @Override
    public List<FilterEntity> addFilters(List<String> filters) {
        return filters.stream().map(this::saveFilter).toList();
    }

    @Override
    public void deleteUnusedFilters() {
        filterRepository.deleteUnusedFilters();
    }

    @Override
    public List<Long> filter(List<Long> chatIds, Link link, String description) {
        return chatIds.stream()
                .filter(chatId -> shouldNotify(chatId, link.url(), description))
                .toList();
    }

    private FilterEntity saveFilter(String filter) {
        String name = filter.split(":")[0];
        String pattern = filter.split(":")[1];
        Optional<FilterEntity> existingFilterEntity = filterRepository.findByNameAndPattern(name, pattern);
        return existingFilterEntity.orElseGet(() -> {
            FilterEntity newFilterEntity = new FilterEntity();
            newFilterEntity.name(name);
            newFilterEntity.pattern(pattern);
            return filterRepository.save(newFilterEntity);
        });
    }

    private boolean shouldNotify(Long chatId, String url, String description) {
        List<FilterEntity> filters = filterRepository.findByChatIdAndLinkUrl(chatId, url);
        return !shouldFilterOutByUser(filters, description);
    }

    private boolean shouldFilterOutByUser(List<FilterEntity> filters, String description) {
        return filters.stream()
                .anyMatch(filter ->
                        filter.name().equals("user") && description.contains("(by " + filter.pattern() + ")"));
    }
}
