package com.joney.coupon.api.dto

import java.time.LocalDateTime

class CreateCouponRequest(
    var name: String,
    var totalQuantity: Int = 5000,
    var validityDays: Int = 7,
    val startsAt: LocalDateTime? = null,
) {
}