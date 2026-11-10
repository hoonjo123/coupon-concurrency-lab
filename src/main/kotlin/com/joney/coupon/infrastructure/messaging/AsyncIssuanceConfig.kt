package com.joney.coupon.infrastructure.messaging

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

const val ISSUANCE_TASK_EXECUTOR = "issuanceTaskExecutor"


// Async를 좀 더 정교하게 다루기 위한 클래스
@Configuration
@EnableAsync
class AsyncIssuanceConfig {

    @Bean(name = [ISSUANCE_TASK_EXECUTOR])
    fun issuanceTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 1
        executor.maxPoolSize = 1
        executor.queueCapacity = 10_000
        executor.setThreadNamePrefix("issuance-async-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }
}