package io.hhplus.concert.infrastructure.reservation

import io.hhplus.concert.domain.reservation.ReservationEvent
import io.hhplus.concert.infrastructure.external.DataPlatformService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
    private val dataPlatformService: DataPlatformService
) {
    companion object {
        private val log = LoggerFactory.getLogger(ReservationEventListener::class.java)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ReservationEvent.Reserve) {
        try {
            dataPlatformService.sendReservationData(event.reservationId)
        } catch (ex: Exception) {
            log.error("데이터 플랫폼으로 데이터 전송 실패 : {}", ex.message)
        }
    }
}