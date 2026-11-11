package com.joney.coupon.domain

import com.joney.coupon.infrastructure.cache.CacheProperties
import com.joney.coupon.infrastructure.cache.CouponCacheRepository
import com.joney.coupon.support.CouponNotFoundException
import org.springframework.stereotype.Service

@Service
class CouponIssuePolicyReader(
    private val couponRepository: CouponRepository,
    private val couponCacheRepository: CouponCacheRepository,
    private val cacheProperties: CacheProperties,
) {

    fun get(id: Long): CouponPolicy  = couponCacheRepository.getIssuePolicyOrLoad(id){
        Thread.sleep(cacheProperties.simulatedLoadLatencyMs)

        couponRepository.findById(id)
            .orElseThrow { CouponNotFoundException() }
            .let(CouponPolicy::from)
    }
}