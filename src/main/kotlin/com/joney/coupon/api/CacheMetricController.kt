package com.joney.coupon.api

import com.joney.coupon.api.dto.CacheMetricResponse
import com.joney.coupon.application.CacheMetrics
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metrics/cache")
class CacheMetricController(
    private val cacheMetrics: CacheMetrics,
) {

    @GetMapping
    fun snapshot(): CacheMetricResponse = CacheMetricResponse.from(cacheMetrics.snapshot())


    @PostMapping("/reset")
    fun reset() {
        cacheMetrics.reset()
    }
}