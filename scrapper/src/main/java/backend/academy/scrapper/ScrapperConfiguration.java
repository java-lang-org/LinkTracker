package backend.academy.scrapper;

import backend.academy.scrapper.client.internal.bot.BotClient;
import backend.academy.scrapper.config.properties.NotificationsTopicProperties;
import backend.academy.scrapper.entity.ChatEntity;
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
import backend.academy.scrapper.service.NotificationSendingService;
import backend.academy.scrapper.service.impl.HttpNotificationSendingService;
import backend.academy.scrapper.service.impl.KafkaNotificationSendingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
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
    public LinkRepository linkRepository(JdbcClient jdbcClient, OrmLinkRepository ormLinkRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlLinkRepository(jdbcClient), ormLinkRepository);
    }

    @Bean
    public TagRepository tagRepository(JdbcClient jdbcClient, OrmTagRepository ormTagRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlTagRepository(jdbcClient), ormTagRepository);
    }

    @Bean
    public FilterRepository filterRepository(JdbcClient jdbcClient, OrmFilterRepository ormFilterRepository) {
        return dataBaseRepositoryFactory.getRepository(new SqlFilterRepository(jdbcClient), ormFilterRepository);
    }

    @Bean
    public ChatLinkRepository chatLinkRepository(
            JdbcClient jdbcClient, ObjectMapper objectMapper, OrmChatLinkRepository ormChatLinkRepository) {
        return dataBaseRepositoryFactory.getRepository(
                new SqlChatLinkRepository(jdbcClient, objectMapper), ormChatLinkRepository);
    }

    @Bean
    public ChatLinkTagRepository chatLinkTagRepository(
            JdbcClient jdbcClient, OrmChatLinkTagRepository ormChatLinkTagRepository) {
        return dataBaseRepositoryFactory.getRepository(
                new SqlChatLinkTagRepository(jdbcClient), ormChatLinkTagRepository);
    }

    @Bean
    public ChatLinkFilterRepository chatLinkFilterRepository(
            JdbcClient jdbcClient, OrmChatLinkFilterRepository ormChatLinkFilterRepository) {
        return dataBaseRepositoryFactory.getRepository(
                new SqlChatLinkFilterRepository(jdbcClient), ormChatLinkFilterRepository);
    }

    @Bean
    public ExecutorService executorService(ScrapperConfig scrapperConfig) {
        return Executors.newFixedThreadPool(scrapperConfig.nThreads());
    }

    @Bean
    public NotificationSendingService notificationSendingService(
            ScrapperConfig scrapperConfig,
            BotClient botClient,
            ObjectMapper objectMapper,
            NotificationsTopicProperties notificationsTopicProperties,
            KafkaTemplate<Long, String> kafkaTemplate) {
        if (scrapperConfig.messageTransport() == ScrapperConfig.MessageTransport.HTTP) {
            return new HttpNotificationSendingService(botClient);
        } else {
            return new KafkaNotificationSendingService(objectMapper, notificationsTopicProperties, kafkaTemplate);
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ChatEntity.class, new ChatEntityDeserializer());

        mapper.registerModule(module);

        return mapper;
    }
}
