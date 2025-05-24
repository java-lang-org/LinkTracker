package backend.academy.scrapper;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.repository.LinkRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LinkMetricsTest {
    private MeterRegistry meterRegistry;

    @Mock
    private LinkRepository linkRepository;

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
    public void testLinkMetrics() {
        // Arrange
        LinkMetrics linkMetrics = new LinkMetrics(meterRegistry, linkRepository);

        when(linkRepository.countByType()).thenReturn(Map.of("GITHUB", 15L, "STACKOVERFLOW", 3L));

        // Act
        linkMetrics.updateLinkMetrics();

        // Assert
        Collection<Gauge> gauges = meterRegistry.find("active_links_by_type").gauges();
        then(gauges.size()).isEqualTo(2);
        for (Gauge gauge : gauges) {
            List<Tag> tags = gauge.getId().getTags();
            then(tags.size()).isEqualTo(1);
            if (tags.getFirst().getValue().equals("GITHUB")) {
                then(gauge.value()).isEqualTo(15);
            } else if (tags.getFirst().getValue().equals("STACKOVERFLOW")) {
                then(gauge.value()).isEqualTo(3);
            }
        }
    }
}
