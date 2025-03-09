package backend.academy.scrapper;

import backend.academy.scrapper.repository.ChatLinkFilterRepository;
import backend.academy.scrapper.repository.ChatLinkRepository;
import backend.academy.scrapper.repository.ChatLinkTagRepository;
import backend.academy.scrapper.repository.ChatRepository;
import backend.academy.scrapper.repository.FilterRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.TagRepository;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.FilterService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.TagService;
import backend.academy.scrapper.service.impl.OrmChatService;
import backend.academy.scrapper.service.impl.OrmFilterService;
import backend.academy.scrapper.service.impl.OrmLinkService;
import backend.academy.scrapper.service.impl.OrmTagService;
import backend.academy.scrapper.service.impl.SqlChatService;
import backend.academy.scrapper.service.impl.SqlFilterService;
import backend.academy.scrapper.service.impl.SqlLinkService;
import backend.academy.scrapper.service.impl.SqlTagService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClient;

@Configuration
@AllArgsConstructor
public class ScrapperConfiguration {
    ScrapperConfig scrapperConfig;
    DataBaseServiceFactory dataBaseServiceFactory;

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
    public ChatService chatService(ChatRepository chatRepository, LinkService linkService) {
        return dataBaseServiceFactory.getService(new SqlChatService(), new OrmChatService(chatRepository, linkService));
    }

    @Bean
    public LinkService linkService(
            TagService tagService,
            FilterService filterService,
            LinkRepository linkRepository,
            ChatLinkRepository chatLinkRepository,
            ChatLinkTagRepository chatLinkTagRepository,
            ChatLinkFilterRepository chatLinkFilterRepository) {
        return dataBaseServiceFactory.getService(
                new SqlLinkService(),
                new OrmLinkService(
                        tagService,
                        filterService,
                        linkRepository,
                        chatLinkRepository,
                        chatLinkTagRepository,
                        chatLinkFilterRepository));
    }

    @Bean
    public TagService tagService(JdbcTemplate jdbcTemplate, TagRepository tagRepository) {
        return dataBaseServiceFactory.getService(new SqlTagService(jdbcTemplate), new OrmTagService(tagRepository));
    }

    @Bean
    public FilterService filterService(JdbcTemplate jdbcTemplate, FilterRepository filterRepository) {
        return dataBaseServiceFactory.getService(
                new SqlFilterService(jdbcTemplate), new OrmFilterService(filterRepository));
    }
}
