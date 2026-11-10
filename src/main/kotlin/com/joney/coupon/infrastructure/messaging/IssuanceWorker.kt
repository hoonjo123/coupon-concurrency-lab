package com.joney.coupon.infrastructure.messaging

import com.joney.coupon.domain.Issuance
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.io.Writer

//SpringEventListener 대신에 kafka consumer 역할
@Component
class IssuanceWorker(
    private val writer: IssuanceWriter,
) {

    @KafkaListener(
        topics = [IssuanceTopics.REQUESTED],
        groupId = IssuanceTopics.CONSUMER_GROUP,
        concurrency = "3",
    )
    fun consume(event: IssuanceRequested) {
        writer.write(event)
    }
}