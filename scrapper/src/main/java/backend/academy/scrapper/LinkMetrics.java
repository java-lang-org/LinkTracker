package backend.academy.scrapper;

import backend.academy.scrapper.repository.LinkRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkMetrics {
    private final MeterRegistry meterRegistry;
    private final LinkRepository linkRepository;
    private final Map<String, AtomicLong> linkCountByType = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 60_000)
    public void updateLinkMetrics() {
        Map<String, Long> counts = linkRepository.countByType();
        updateMetrics(counts);
    }

    private void updateMetrics(Map<String, Long> counts) {
        counts.forEach((type, count) -> linkCountByType
                .computeIfAbsent(type, t -> {
                    AtomicLong value = new AtomicLong(0);
                    Gauge.builder("active_links_by_type", value, AtomicLong::get)
                            .tag("type", t)
                            .register(meterRegistry);
                    return value;
                })
                .set(count));
    }
}
