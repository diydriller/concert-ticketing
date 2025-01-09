package io.hhplus.concert.domain.reservation

interface ReservationReader {
    fun findReservation(userId: String, scheduleId: String, seatId: String): Reservation?
}