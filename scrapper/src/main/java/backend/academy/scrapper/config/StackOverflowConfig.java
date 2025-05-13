package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stackoverflow")
public record StackOverflowConfig(String key, String accessToken, String baseUrl, TimeoutsInSec timeoutsInSec) {}
