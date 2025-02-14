package io.hhplus.concert.domain.reservation

class ReservationEvent {
    data class Reserve(
        val reservationId: String
    )
}