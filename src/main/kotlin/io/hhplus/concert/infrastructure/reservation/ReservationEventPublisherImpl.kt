package io.hhplus.concert.infrastructure.reservation

import io.hhplus.concert.domain.reservation.ReservationEvent
import io.hhplus.concert.domain.reservation.ReservationEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ReservationEventPublisherImpl(
    private val applicationEventPublisher: ApplicationEventPublisher
): ReservationEventPublisher {
    override fun publish(event: ReservationEvent.Reserve) {
        applicationEventPublisher.publishEvent(event)
    }
}