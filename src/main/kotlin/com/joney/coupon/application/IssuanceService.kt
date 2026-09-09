package com.joney.coupon.application

import com.joney.coupon.domain.Issuance
import com.joney.coupon.domain.IssuanceRepository
import com.joney.coupon.domain.IssuanceStatus
import com.joney.coupon.support.AlreadyUsedException
import com.joney.coupon.support.ExpiredException
import com.joney.coupon.support.IssuanceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class IssuanceService(
    private val issuanceRepository: IssuanceRepository,
) {
    @Transactional
    fun use(issuanceId: Long, userId: Long): Issuance {
        var issuance = issuanceRepository.findById(issuanceId)
            .orElseThrow { IssuanceNotFoundException() }

        when (issuance.status) {
            IssuanceStatus.USED -> throw AlreadyUsedException()
            IssuanceStatus.EXPIRED -> throw ExpiredException()
            IssuanceStatus.ISSUED -> Unit
        }
        var now = LocalDateTime.now()
        if (issuance.isExpired(now)) {
            throw ExpiredException()
        }

        issuance.markUsed(now)
        return issuance
    }

    fun findByUser(userId: Long): List<Issuance> =
        issuanceRepository.findByUserIdOrderByIssuedAtDesc(userId)
}