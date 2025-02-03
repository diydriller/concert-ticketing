package io.hhplus.concert.infrastructure.reservation

import io.hhplus.concert.domain.reservation.Reservation
import io.hhplus.concert.domain.reservation.ReservationReader
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ReservationReaderImpl(
    private val reservationRepository: ReservationRepository
) : ReservationReader {
    override fun findReservation(userId: String, scheduleId: String, seatId: String): Reservation? {
        return reservationRepository.findByUserIdAndScheduleIdAndSeatIdAndStatus(
            userId,
            scheduleId,
            seatId,
            Reservation.Status.RESERVED
        )
    }

    override fun findReservation(reservationId: String): Reservation? {
        return reservationRepository.findReservationById(reservationId)
    }

    override fun findReservationForUpdate(reservationId: String): Reservation? {
        return reservationRepository.findReservationByIdForUpdate(reservationId)
    }
}