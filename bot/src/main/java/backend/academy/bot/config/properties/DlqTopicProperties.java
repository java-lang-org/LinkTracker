package backend.academy.bot.config.properties;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DlqTopicProperties {
    private final String topic = "dlq";
    private final int partitions = 1;
    private final short replicas = 1;

    public NewTopic toNewTopic() {
        return new NewTopic(topic, partitions, replicas);
    }

    public int partition() {
        return 0;
    }
}
