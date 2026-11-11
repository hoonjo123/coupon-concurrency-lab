package com.joney.coupon.domain

import java.time.LocalDateTime

class CouponPolicy(
    var startsAt: LocalDateTime?,
    var validityDays: Int,
) {
    fun isBookingOpen(now: LocalDateTime): Boolean =
        startsAt?.let { !now.isBefore(it) } ?: true


    companion object {
        fun from(coupon: Coupon) : CouponPolicy = CouponPolicy(
            startsAt = coupon.startsAt,
            validityDays = coupon.validityDays,
        )
    }
}