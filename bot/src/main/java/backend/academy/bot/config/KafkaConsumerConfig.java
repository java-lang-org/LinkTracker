package backend.academy.bot.config;

import backend.academy.bot.config.properties.DlqTopicProperties;
import backend.academy.dto.LinkUpdate;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<Long, LinkUpdate> consumerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildConsumerProperties(null);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "backend.academy.dto");

        return new DefaultKafkaConsumerFactory<>(
            props,
            new LongDeserializer(),
            new JsonDeserializer<>(LinkUpdate.class));
    }

    @Bean
    public KafkaTemplate<Long, LinkUpdate> kafkaTemplate(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<Long, LinkUpdate> factory = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public DefaultErrorHandler errorHandler(
        KafkaTemplate<Long, LinkUpdate> kafkaTemplate,
        DlqTopicProperties dlqTopicProperties) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (consumerRecord, e) -> new TopicPartition(dlqTopicProperties.topic(), dlqTopicProperties.partition())),
            new FixedBackOff(500L, 1));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> kafkaListenerContainerFactory(
        ConsumerFactory<Long, LinkUpdate> consumerFactory,
        DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<Long, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
