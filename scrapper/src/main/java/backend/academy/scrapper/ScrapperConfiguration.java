package backend.academy.scrapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ScrapperConfiguration {
    @Bean(name = "gitHubRestClient")
    public RestClient gitHubRestClient(ScrapperConfig scrapperConfig) {
        return RestClient.builder()
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .defaultHeader("Authorization", "Bearer " + scrapperConfig.githubToken())
            .build();
    }

    @Bean(name = "stackOverflowRestClient")
    public RestClient stackOverflowClient() {
        return RestClient.builder().build();
    }

    @Bean(name = "botRestClient")
    public RestClient botRestClient() {
        return RestClient.builder().build();
    }
}
