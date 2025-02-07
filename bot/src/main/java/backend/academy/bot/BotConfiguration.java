package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BotConfiguration {
    @Bean
    public TelegramBot telegramBot(BotConfig botConfig) {
        return new TelegramBot(botConfig.telegramToken());
    }

    @Bean
    public ExecutorService executorService(BotConfig botConfig) {
        return Executors.newFixedThreadPool(botConfig.nThreads());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
