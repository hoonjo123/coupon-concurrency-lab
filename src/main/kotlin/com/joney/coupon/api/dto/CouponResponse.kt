package com.joney.coupon.api.dto

import com.joney.coupon.domain.Coupon
import java.time.LocalDateTime

class CouponResponse(
    var id: Long,
    var name: String,
    var totalQuantity: Int,
    var issuedQuantity: Int,
    var validityDays: Int,
    var startAt: LocalDateTime?,
    var createdAt: LocalDateTime,
) {
    companion object {
        fun from(coupon: Coupon): CouponResponse = CouponResponse(
            id = requireNotNull(coupon.id),
            name = coupon.name,
            totalQuantity = coupon.totalQuantity,
            issuedQuantity = coupon.issuedQuantity,
            validityDays = coupon.validityDays,
            startAt = coupon.startsAt,
            createdAt = coupon.createdAt,
        )
    }
}