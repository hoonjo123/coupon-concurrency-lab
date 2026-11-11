package com.joney.coupon.application

import com.joney.coupon.api.dto.CreateCouponRequest
import com.joney.coupon.domain.Coupon
import com.joney.coupon.domain.CouponIssuePolicyReader
import com.joney.coupon.domain.CouponRepository
import com.joney.coupon.domain.Issuance
import com.joney.coupon.domain.IssuanceRepository
import com.joney.coupon.infrastructure.cache.CacheProperties
import com.joney.coupon.infrastructure.messaging.IssuanceRequestProducer
import com.joney.coupon.infrastructure.messaging.IssuanceRequested
import com.joney.coupon.support.AlreadyIssuedException
import com.joney.coupon.support.CouponNotFoundException
import com.joney.coupon.support.NotStartedException
import com.joney.coupon.support.SoldOutException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class CouponService(
    private val couponRepository: CouponRepository,
//    더이상 해당 DB에 접근하지 않음으로 주석처리
//    private val issuanceRepository: IssuanceRepository,
    private val couponIssuer: CouponIssuer,
//    이벤트 퍼블리셔를 사용할 예정이기에 주석 처리
//    private val issuanceQueue: InMemoryIssuanceQueue,

//    카프카 추가됨에 따라 주석처리
//    private val eventPublisher: ApplicationEventPublisher,

//    private val cacheProperties: CacheProperties,
//    private val cacheMetrics: CacheMetrics,

    private val issuanceRequestProducer: IssuanceRequestProducer,

    private val couponIssuePolicyReader: CouponIssuePolicyReader,
) {
    @Transactional
    fun createCoupon(request: CreateCouponRequest): Coupon {
        var coupon = couponRepository.save(
            Coupon(
                name = request.name,
                totalQuantity = request.totalQuantity,
                validityDays = request.validityDays,
                startsAt = request.startsAt,
            )
        )
        couponIssuer.initStock(coupon.id!!, coupon.totalQuantity)
        return coupon
    }

    @Transactional
    fun issue(couponId: Long, userId: Long): Issuance {

        var policy = couponIssuePolicyReader.get(couponId)
//        cacheMetrics.incrementCouponDbRead()
        // 다른 리전이라는 가정하에 100ms 정도 지연을 줘서 테스트 할 예정
//        Thread.sleep(cacheProperties.simulatedLoadLatencyMs)
//
//
//        val coupon = couponRepository.findById(couponId)
//            .orElseThrow { CouponNotFoundException() }

        val now = LocalDateTime.now()

        if (!policy.isBookingOpen(now)) {
            throw NotStartedException()
        }
//        if (coupon.isSoldOut()) {
//            throw SoldOutException()
//        } -> 매진 이미 레디스 안에서 검사하고있음
//        if (issuanceRepository.existsByUserIdAndCouponId(userId, couponId)) {
//            throw AlreadyIssuedException()
//        }


        couponIssuer.tryIssue(couponId, userId)

        val expiredsAt = now.plusDays(policy.validityDays.toLong())
        issuanceRequestProducer.publish(
            IssuanceRequested(
                couponId = couponId,
                userId = userId,
                issuedAt = now,
                expiresAt = expiredsAt,
            )
        )
        return Issuance(
            userId = userId,
            couponId = couponId,
            issuedAt = now,
            expiresAt = expiredsAt,
        )

//        아래 두개 worker한테 보낼거기때문에 주석처리함
//        couponRepository.incrementIssuedQuantity(couponId)
//
//
//        return issuanceRepository.save(
//            Issuance(
//                userId = userId,
//                couponId = couponId,
//                issuedAt = now,
//                expiresAt = now.plusDays(coupon.validityDays.toLong()),
//            )
//        )
    }
}