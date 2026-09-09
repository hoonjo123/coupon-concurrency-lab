package com.joney.coupon.api.dto

import com.joney.coupon.domain.Issuance
import com.joney.coupon.domain.IssuanceStatus
import java.time.LocalDateTime

class IssusanceResponse(
    var id: Long,
    var userId: Long,
    var couponId: Long,
    var status: IssuanceStatus,
    var issuedAt: LocalDateTime,
    var expiresAt: LocalDateTime,
    var usedAt: LocalDateTime?,
) {
    companion object {
        fun from(issuance: Issuance): IssusanceResponse = IssusanceResponse(
            id = requireNotNull(issuance.id),
            userId = issuance.userId,
            couponId = issuance.couponId,
            status = issuance.status,
            issuedAt = issuance.issuedAt,
            expiresAt = issuance.expiresAt,
            usedAt = issuance.usedAt,
        )
    }
}