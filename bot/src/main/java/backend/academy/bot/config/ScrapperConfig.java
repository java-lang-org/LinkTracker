package backend.academy.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scrapper")
public record ScrapperConfig(String url, TimeoutsInSec timeoutsInSec) {
    public record TimeoutsInSec(int connect, int connectionRequest, int read) {}
}
