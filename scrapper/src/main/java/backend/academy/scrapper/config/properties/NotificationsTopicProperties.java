package backend.academy.scrapper.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class NotificationsTopicProperties {
    @Value("${app.notifications.topic}")
    private String topic;

    @Value("${app.notifications.partitions}")
    private int partitions;

    @Value("${app.notifications.replicas}")
    private short replicas;

    public NewTopic toNewTopic() {
        return new NewTopic(topic, partitions, replicas);
    }
}
