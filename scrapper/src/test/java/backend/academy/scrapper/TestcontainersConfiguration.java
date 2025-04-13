package backend.academy.scrapper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    @Bean
    @RestartScope
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
                .withExposedPorts(5432)
                .withDatabaseName("scrapper")
                .withUsername("postgres")
                .withPassword("postgres");

        postgres.start();
        applyLiquibaseMigrations(postgres);

        return postgres;
    }

    @Bean
    @RestartScope
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        KafkaContainer kafkaContainer = new KafkaContainer("apache/kafka-native:3.8.1").withExposedPorts(9092);

        kafkaContainer.setPortBindings(List.of("9092:9092"));
        kafkaContainer.start();

        return kafkaContainer;
    }

    private void applyLiquibaseMigrations(PostgreSQLContainer<?> postgres) {
        try (Connection connection =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Path migrationsPath = Path.of("../migrations");
            Liquibase liquibase = new Liquibase("master.xml", new DirectoryResourceAccessor(migrationsPath), database);
            liquibase.update();
        } catch (Exception e) {
            throw new RuntimeException("Error applying Liquibase migrations", e);
        }
    }
}
