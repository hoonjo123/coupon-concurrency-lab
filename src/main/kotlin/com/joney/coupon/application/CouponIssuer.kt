package com.joney.coupon.application

import com.joney.coupon.support.SoldOutException
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component

@Component
class CouponIssuer(
    private val redisTemplate: StringRedisTemplate,
) {

    private val script: RedisScript<Long> = RedisScript.of(
        ClassPathResource("lua/issue.lua"),
        Long::class.java,
    )

    fun tryIssue(couponId: Long) {
        val raw = redisTemplate.execute(
            script,
            listOf()
        ) ?: error("?Lua 스크립트 결과가 null입니다")

        when (raw) {
            1L -> Unit
            0L -> throw SoldOutException()
            else -> error("예상치 못한 Lua 결과: $raw")
        }
    }

    fun initStock(coupondId: Long, totalQuantity: Int) {
        redisTemplate.opsForValue().set(stockKey(coupondId), totalQuantity.toString())
    }

    private fun stockKey(couponId: Long) = "coupon:$couponId:stock"
}