package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiting")
public record RateLimitingConfig(int maxRequestsPerMinute) {}
