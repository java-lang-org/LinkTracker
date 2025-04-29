package backend.academy.bot;

import backend.academy.bot.config.BotConfig;
import backend.academy.bot.config.RateLimitingConfig;
import backend.academy.bot.config.RetryConfig;
import backend.academy.bot.config.ScrapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class, ScrapperConfig.class, RetryConfig.class, RateLimitingConfig.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
