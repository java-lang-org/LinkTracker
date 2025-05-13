package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github")
public record GitHubConfig(String token, String baseUrl, TimeoutsInSec timeoutsInSec) {}
