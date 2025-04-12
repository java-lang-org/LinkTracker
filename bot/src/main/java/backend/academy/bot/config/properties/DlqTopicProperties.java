package backend.academy.bot.config.properties;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DlqTopicProperties {
    @Value("${app.notifications.dlq}")
    private String topic;

    private final int partitions = 1;
    private final short replicas = 1;

    public NewTopic toNewTopic() {
        return new NewTopic(topic, partitions, replicas);
    }

    public int partition() {
        return 0;
    }
}
