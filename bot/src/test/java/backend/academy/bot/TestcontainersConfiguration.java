package backend.academy.bot;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection("redis")
    public GenericContainer<?> redisContainer() {
        GenericContainer<?> genericContainer =
                new GenericContainer<>(DockerImageName.parse("redis:7.4.2")).withExposedPorts(6379);
        genericContainer.start();
        return genericContainer;
    }

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);

        kafkaContainer.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());

        return kafkaContainer;
    }
}
