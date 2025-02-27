package io.hhplus.concert.infrastructure.reservation

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.concert.domain.reservation.ReservationEvent
import io.hhplus.concert.infrastructure.external.DataPlatformService
import io.hhplus.concert.util.StringUtil.Companion.RESERVE_EVENT_TOPIC
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
    private val dataPlatformService: DataPlatformService,
    private val objectMapper: ObjectMapper
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

    @KafkaListener(topics = [RESERVE_EVENT_TOPIC], groupId = "data-platform-group")
    fun createUserNode(message: String, acknowledgment: Acknowledgment) {
        try {
            val event = objectMapper.readValue(message, ReservationEvent.Reserve::class.java)
            dataPlatformService.sendReservationData(event.reservationId)
            acknowledgment.acknowledge()
        } catch (ex: Exception) {
            log.error("데이터 플랫폼으로 데이터 전송 실패 : {}", ex.message)
        }
    }
}