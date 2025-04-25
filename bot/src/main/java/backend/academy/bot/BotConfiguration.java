package backend.academy.bot;

import backend.academy.bot.config.BotConfig;
import backend.academy.bot.config.ScrapperConfig;
import com.pengrad.telegrambot.TelegramBot;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

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
    public RestClient restClient(ScrapperConfig scrapperConfig) {
        return RestClient.builder()
                .requestFactory(clientHttpRequestFactory(scrapperConfig))
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(ScrapperConfig scrapperConfig) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(
                Duration.ofSeconds(scrapperConfig.timeoutsInSec().connect()));
        factory.setConnectionRequestTimeout(
                Duration.ofSeconds(scrapperConfig.timeoutsInSec().connectionRequest()));
        factory.setReadTimeout(Duration.ofSeconds(scrapperConfig.timeoutsInSec().read()));
        return factory;
    }
}
