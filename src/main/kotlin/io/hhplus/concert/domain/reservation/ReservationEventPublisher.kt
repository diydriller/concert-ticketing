package io.hhplus.concert.domain.reservation

interface ReservationEventPublisher {
    fun publish(event: ReservationEvent.Reserve)
}