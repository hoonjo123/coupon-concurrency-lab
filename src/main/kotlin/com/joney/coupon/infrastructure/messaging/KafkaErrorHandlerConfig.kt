package com.joney.coupon.infrastructure.messaging

import org.apache.kafka.common.TopicPartition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaErrorHandlerConfig {

    @Bean
    fun errorHandler(template: KafkaTemplate<String, Any>): DefaultErrorHandler {
        val recoverer = DeadLetterPublishingRecoverer(template) { record, _ ->
            TopicPartition("${record.topic()}.DLT", record.partition())
        }
//        컨슈머가 메시지 처리에 실패하면 1초 간격으로 3번 재시도
        return DefaultErrorHandler(recoverer, FixedBackOff(1_000L, 3L))
    }
}
