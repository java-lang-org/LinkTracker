package backend.academy.scrapper;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMessageMetrics {
    private final Counter userMessageCounter;

    public UserMessageMetrics(MeterRegistry meterRegistry) {
        this.userMessageCounter = init(meterRegistry);
    }

    private Counter init(MeterRegistry meterRegistry) {
        return Counter.builder("user.messages.total")
                .description("Total number of user messages")
                .register(meterRegistry);
    }

    public void increment() {
        userMessageCounter.increment();
    }
}
