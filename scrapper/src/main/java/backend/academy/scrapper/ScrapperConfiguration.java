package backend.academy.scrapper;

import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.repository.impl.OrmChatLinkFilterRepository;
import backend.academy.scrapper.repository.impl.OrmChatLinkRepository;
import backend.academy.scrapper.repository.impl.OrmChatLinkTagRepository;
import backend.academy.scrapper.repository.impl.OrmChatRepository;
import backend.academy.scrapper.repository.impl.OrmFilterRepository;
import backend.academy.scrapper.repository.impl.OrmLinkRepository;
import backend.academy.scrapper.repository.impl.OrmTagRepository;
import backend.academy.scrapper.repository.impl.SqlChatLinkFilterRepository;
import backend.academy.scrapper.repository.impl.SqlChatLinkRepository;
import backend.academy.scrapper.repository.impl.SqlChatLinkTagRepository;
import backend.academy.scrapper.repository.impl.SqlChatRepository;
import backend.academy.scrapper.repository.impl.SqlFilterRepository;
import backend.academy.scrapper.repository.impl.SqlLinkRepository;
import backend.academy.scrapper.repository.impl.SqlTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.RestClient;

@Configuration
@AllArgsConstructor
public class ScrapperConfiguration {
    ScrapperConfig scrapperConfig;
    DataBaseRepositoryFactory dataBaseRepositoryFactory;

    @Bean(name = "gitHubRestClient")
    public RestClient gitHubRestClient() {
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

    @Bean
    public ChatRepository chatRepository(JdbcClient jdbcClient, OrmChatRepository ormChatRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlChatRepository(jdbcClient), ormChatRepository);
    }

    @Bean
    LinkRepository linkRepository(JdbcClient jdbcClient, OrmLinkRepository ormLinkRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlLinkRepository(jdbcClient), ormLinkRepository);
    }

    @Bean
    TagRepository tagRepository(JdbcClient jdbcClient, OrmTagRepository ormTagRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlTagRepository(jdbcClient), ormTagRepository);
    }

    @Bean
    FilterRepository filterRepository(JdbcClient jdbcClient, OrmFilterRepository ormFilterRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlFilterRepository(jdbcClient), ormFilterRepository);
    }

    @Bean
    ChatLinkRepository chatLinkRepository(JdbcClient jdbcClient, OrmChatLinkRepository ormChatLinkRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlChatLinkRepository(jdbcClient), ormChatLinkRepository);
    }

    @Bean
    ChatLinkTagRepository chatLinkTagRepository(
            JdbcClient jdbcClient, OrmChatLinkTagRepository ormChatLinkTagRepository) {
        return dataBaseRepositoryFactory.getRepository(
                new SqlChatLinkTagRepository(jdbcClient), ormChatLinkTagRepository);
    }

    @Bean
    ChatLinkFilterRepository chatLinkFilterRepository(
            JdbcClient jdbcClient, OrmChatLinkFilterRepository ormChatLinkFilterRepository) {
        return dataBaseRepositoryFactory.getRepository(
                new SqlChatLinkFilterRepository(jdbcClient), ormChatLinkFilterRepository);
    }
}
