package backend.academy.scrapper;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapeMetrics {
    private final MeterRegistry meterRegistry;

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordScrape(String type, Timer.Sample sample) {
        sample.stop(Timer.builder("scrape.duration")
                .tag("type", type)
                .publishPercentileHistogram()
                .register(meterRegistry));
    }
}
