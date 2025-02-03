package io.hhplus.concert.aop.lock

import jakarta.transaction.Transactional
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component


@Component
class AopForTransaction {
    @Transactional
    @Throws(Throwable::class)
    fun proceed(joinPoint: ProceedingJoinPoint): Any? {
        return joinPoint.proceed()
    }
}