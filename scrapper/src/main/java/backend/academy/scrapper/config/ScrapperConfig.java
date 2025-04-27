package backend.academy.scrapper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ScrapperConfig(DataBase dataBase, int nThreads, MessageTransport messageTransport) {
    public record DataBase(AccessType accessType, int batchSize) {
        public enum AccessType {
            SQL,
            ORM
        }
    }

    public enum MessageTransport {
        HTTP,
        Kafka
    }
}
