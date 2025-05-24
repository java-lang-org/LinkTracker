package backend.academy.scrapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScrapeMetricsTest {
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        Metrics.globalRegistry.add(meterRegistry);
    }

    @AfterEach
    void tearDown() {
        meterRegistry.clear();
        Metrics.globalRegistry.clear();
    }

    @Test
    public void testScrapeMetrics() throws InterruptedException {
        // Arrange
        ScrapeMetrics scrapeMetrics = new ScrapeMetrics(meterRegistry);

        // Act
        Timer.Sample sample = scrapeMetrics.startTimer();
        Thread.sleep(1_000);
        scrapeMetrics.recordScrape("github", sample);

        // Assert
        Timer timer =
                meterRegistry.find("scrape.duration").tag("type", "github").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(TimeUnit.SECONDS)).isGreaterThan(0.9);
    }
}
