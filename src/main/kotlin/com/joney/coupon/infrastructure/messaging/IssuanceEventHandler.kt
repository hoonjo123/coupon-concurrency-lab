//package com.joney.coupon.infrastructure.messaging
//
//import org.springframework.context.event.EventListener
//import org.springframework.scheduling.annotation.Async
//import org.springframework.stereotype.Component
//
//@Component
//class IssuanceEventHandler(
//    private val writer: IssuanceWriter,
//) {
////    기존에 InMemoryIssuanceQueue 를 사용하여 큐를 통해 이벤트를 처리하던 방식을 스프링 이벤트 퍼블리셔를 사용하도록 변경
//    @Async(ISSUANCE_TASK_EXECUTOR)
//    @EventListener
//    fun handle(event: IssuanceRequested) {
//        writer.write(event)
//    }
//}