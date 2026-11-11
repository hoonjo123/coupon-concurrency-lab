package com.joney.coupon.api.dto

import com.joney.coupon.application.CacheMetricsSnapshot

class CacheMetricResponse(
    val couponDbReads: Long,
    val couponCacheHits: Long,
    val soldOutRedisExists: Long,
    val soldOutFastPathHits: Long, //L1캐시
) {
    companion object {
        fun from(snapshot: CacheMetricsSnapshot): CacheMetricResponse = CacheMetricResponse(
            couponDbReads = snapshot.couponDbReads,
            couponCacheHits = snapshot.couponCacheHits,
            soldOutRedisExists = snapshot.soldOutRedisExists,
            soldOutFastPathHits = snapshot.soldOutFastPathHits,
        )
    }
}