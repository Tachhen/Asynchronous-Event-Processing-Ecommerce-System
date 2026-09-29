package com.EPS.eps.Kafka.ErrorHandling;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory.DeadLetterPublisherCreator;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandling {
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String,Object>kafkaTemplate){
        DeadLetterPublishingRecoverer recoverer=new DeadLetterPublishingRecoverer(
                            kafkaTemplate,
                            (record,exception)->
                                new TopicPartition(
                                    record.topic() + ".DLT",
                                    record.partition()
                                )
                    );
        FixedBackOff backOff=new FixedBackOff(1000L,2L);
        return new DefaultErrorHandler(
            recoverer,
            backOff
        );
    }
}
