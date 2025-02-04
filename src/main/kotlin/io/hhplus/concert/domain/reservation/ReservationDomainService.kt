package io.hhplus.concert.domain.reservation

import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.domain.concert.Seat.Status
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.response.BaseResponseStatus
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReservationDomainService(
    private val reservationReader: ReservationReader
) {
    fun reserve(seat: Seat, reservation: Reservation, userId: String, scheduleId: String) {
        reservationReader.findReservation(userId, scheduleId, seat.id)?.let { savedReservation ->
            if (savedReservation.isReserved()) throw ConflictException(BaseResponseStatus.ALREADY_RESERVED)
        }

        if (isHolding(seat)) {
            throw ConflictException(BaseResponseStatus.ALREADY_HOLDING_SEAT)
        }
        if (seat.status == Status.RESERVED) {
            throw ConflictException(BaseResponseStatus.ALREADY_RESERVED)
        }
        seat.status = Status.HOLD
        seat.holdExpiration = LocalDateTime.now().plusMinutes(5)

        reservation.reserve()
    }

    fun complete(seat: Seat, concertSchedule: ConcertSchedule, reservation: Reservation) {
        if (!isHolding(seat)) {
            seat.expire()
            throw ConflictException(BaseResponseStatus.NOT_HOLDING_SEAT)
        }
        seat.status = Status.RESERVED
        reservation.status = Reservation.Status.COMPLETED
        concertSchedule.reservedSeatCount++
    }

    fun isHolding(seat: Seat): Boolean {
        return seat.status == Status.HOLD && (seat.holdExpiration != null && seat.holdExpiration!!.isAfter(LocalDateTime.now()))
    }
}