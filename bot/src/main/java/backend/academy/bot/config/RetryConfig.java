package backend.academy.bot.config;

import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retry")
public record RetryConfig(long backOffPeriod, int maxAttempts, Set<Integer> retryableStatuses) {}
