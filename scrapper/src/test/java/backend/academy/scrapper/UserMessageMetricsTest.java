package backend.academy.scrapper;

import static org.assertj.core.api.BDDAssertions.then;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserMessageMetricsTest {
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
    void testUserMessageMetrics() {
        // Arrange
        UserMessageMetrics userMessageMetrics = new UserMessageMetrics(meterRegistry);

        // Act
        for (int i = 0; i < 3; i++) {
            userMessageMetrics.increment();
        }

        // Assert
        Counter counter = meterRegistry.find("user.messages.total").counter();
        then(counter).isNotNull();
        then(counter.count()).isEqualTo(3);
    }
}
