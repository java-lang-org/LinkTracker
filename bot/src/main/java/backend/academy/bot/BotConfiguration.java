package backend.academy.bot;

import backend.academy.bot.config.BotConfig;
import backend.academy.bot.config.HttpStatusRetryPolicy;
import backend.academy.bot.config.RateLimitingConfig;
import backend.academy.bot.config.RateLimitingFilter;
import backend.academy.bot.config.RetryConfig;
import backend.academy.bot.config.ScrapperConfig;
import com.pengrad.telegrambot.TelegramBot;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
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
                .defaultStatusHandler(status -> true, (request, response) -> {})
                .build();
    }

    @Bean
    public RetryTemplate retryTemplate(RetryConfig retryConfig) {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(retryConfig.backOffPeriod());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        retryTemplate.setRetryPolicy(
                new HttpStatusRetryPolicy(retryConfig.maxAttempts(), retryConfig.retryableStatuses()));

        return retryTemplate;
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter(RateLimitingConfig rateLimitingConfig) {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitingFilter(rateLimitingConfig.maxRequestsPerMinute()));
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
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
