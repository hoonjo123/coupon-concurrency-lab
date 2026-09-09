package com.joney.coupon.application

import com.joney.coupon.api.dto.CreateCouponRequest
import com.joney.coupon.domain.Coupon
import com.joney.coupon.domain.CouponRepository
import com.joney.coupon.domain.Issuance
import com.joney.coupon.domain.IssuanceRepository
import com.joney.coupon.support.AlreadyIssuedException
import com.joney.coupon.support.CouponNotFoundException
import com.joney.coupon.support.NotStartedException
import com.joney.coupon.support.SoldOutException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val issuanceRepository: IssuanceRepository,
) {
    @Transactional
    fun createCoupon(request: CreateCouponRequest) : Coupon {
        var coupon = Coupon (
            name = request.name,
            totalQuantity = request.totalQuantity,
            validityDays = request.validityDays,
            startsAt = request.startsAt,
        )
        return couponRepository.save(coupon)
    }

    @Transactional
    fun issue(couponId: Long, userId: Long): Issuance {
        val coupon = couponRepository.findById(couponId)
            .orElseThrow { CouponNotFoundException() }

        val now = LocalDateTime.now()

        if (!coupon.isBookingOpen(now)) {
            throw NotStartedException()
        }
        if (coupon.isSoldOut()) {
            throw SoldOutException()
        }
        if (issuanceRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw AlreadyIssuedException()
        }

        coupon.issuedQuantity++

        return issuanceRepository.save(
            Issuance(
                userId = userId,
                couponId = couponId,
                issuedAt = now,
                expiresAt = now.plusDays(coupon.validityDays.toLong()),
            )
        )
    }
}