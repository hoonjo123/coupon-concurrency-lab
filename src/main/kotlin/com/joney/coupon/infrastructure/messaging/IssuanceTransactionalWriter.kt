package com.joney.coupon.infrastructure.messaging

import com.joney.coupon.domain.CouponRepository
import com.joney.coupon.domain.Issuance
import com.joney.coupon.domain.IssuanceRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class IssuanceTransactionalWriter(
    private val issuanceRepository: IssuanceRepository,
    private val couponRepository: CouponRepository
) {

    @Transactional
    fun insertAndIncrement(event: IssuanceRequested) {
        issuanceRepository.save(
            Issuance(
                userId = event.userId,
                couponId = event.couponId,
                issuedAt = event.issuedAt,
                expiresAt = event.expiresAt,
            )
        )
        couponRepository.incrementIssuedQuantity(event.couponId)
    }
}