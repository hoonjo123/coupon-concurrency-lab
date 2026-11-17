package com.joney.coupon.infrastructure.cache

import com.joney.coupon.application.CacheMetrics
import com.joney.coupon.domain.CouponIssuePolicy
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Repository
class CouponCacheRepository(
    private val redis: StringRedisTemplate,
    private val mapper: ObjectMapper,
    private val properties: CacheProperties,
    private val cacheMetrics: CacheMetrics,
) {
    private val singleFlightScript = listLuaScript("lua/cache-single-flight.lua")
    private val releaseLockScript = longlistLuaScript("lua/release-lock.lua")

    fun getIssuePolicyOrLoad(id: Long, loader: () -> CouponIssuePolicy) =
        getOrLoad(
            cacheKey = "coupon:$id:issue-policy",
            lockKey = "coupon:$id:issue-policy:lock",
            loader = loader,
        )

    private fun getOrLoad(
        cacheKey: String,
        lockKey: String,
        loader: () -> CouponIssuePolicy,
    ): CouponIssuePolicy {
//        redis.opsForValue().get(cacheKey)?.let {
//            cached -> cacheMetrics.incrementCouponCacheHits()
//            return mapper.readValue(cached, CouponPolicy::class.java)
//        }
//        cacheMetrics.incrementCouponDbRead()
//
//        //cache 가 miss 된 경우, loader 를 통해서 DB에서 가져오고, cache에 저장
//        val response = loader()
//        redis.opsForValue().set(cacheKey, mapper.writeValueAsString(response), Duration.ofMillis(properties.ttlMs))
//
//        return response
        val token = UUID.randomUUID().toString()
        repeat(MAX_RETRIES) {
            val result = redis.runForString(
                singleFlightScript,
                listOf(cacheKey, lockKey),
                token, LOCK_TTL_MS
            )

            when (result[0]) {
                "HIT" -> {
                    cacheMetrics.incrementCouponCacheHits()
                    return mapper.readValue(result[1], CouponIssuePolicy::class.java)
                }
                "LOAD" -> return try{
                    cacheMetrics.incrementCouponDbRead()
                    val response = loader()
                    val json = mapper.writeValueAsString(response)
                    redis.opsForValue().set(cacheKey, json, java.time.Duration.ofMillis(properties.ttlMs))
                    response
                } finally {
                    redis.runForLong(
                        releaseLockScript,
                        listOf(lockKey),
                        token,
                    )
                }
                // 너무 많은 CPU를 점유하지 않도록 20ms 정도 sleep 후, 최대 50번 재시도
                // 1초동안 50번 반복
                "WAIT" -> {
                    Thread.sleep(WAIT_BACKOFF_MS)
                }
            }
        }
        throw IllegalStateException("쿠폰 캐시 채우기 timeout (key=$cacheKey")
    }

    private companion object {
        const val MAX_RETRIES = 50
        const val WAIT_BACKOFF_MS = 20L
        const val LOCK_TTL_MS = 3_000L
    }

}