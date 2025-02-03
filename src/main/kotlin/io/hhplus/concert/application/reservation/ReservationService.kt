package io.hhplus.concert.application.reservation

import io.hhplus.concert.aop.lock.DistributedLock
import io.hhplus.concert.domain.concert.ConcertReader
import io.hhplus.concert.domain.concert.ConcertStore
import io.hhplus.concert.domain.reservation.Reservation
import io.hhplus.concert.domain.reservation.ReservationDomainService
import io.hhplus.concert.domain.reservation.ReservationReader
import io.hhplus.concert.domain.reservation.ReservationStore
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.response.BaseResponseStatus
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ReservationService(
    private val reservationStore: ReservationStore,
    private val concertReader: ConcertReader,
    private val concertStore: ConcertStore,
    private val reservationDomainService: ReservationDomainService
) {
    @Transactional
    fun reserveConcert(command: ReservationCommand): Reservation {
        val schedule = concertReader.findConcertSchedule(command.scheduleId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT_SCHEDULE)

        val seat = concertReader.findSeatForUpdate(command.seatId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_SEAT)

        val reservation = Reservation(
            concertScheduleId = schedule.id,
            seatId = seat.id,
            userId = command.userId,
            price = seat.price
        )

        reservationDomainService.reserve(seat, reservation, command.userId, command.scheduleId)

        concertStore.saveSeat(seat)
        reservationStore.saveReservation(reservation)

        return reservationStore.saveReservation(reservation)
    }

    @DistributedLock(key = "reservation-#command.scheduleId-#command.seatId")
    fun reserveConcertWithRedissonLock(command: ReservationCommand): Reservation {
        val schedule = concertReader.findConcertSchedule(command.scheduleId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT_SCHEDULE)

        val seat = concertReader.findSeat(command.seatId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_SEAT)

        val reservation = Reservation(
            concertScheduleId = schedule.id,
            seatId = seat.id,
            userId = command.userId,
            price = seat.price
        )

        reservationDomainService.reserve(seat, reservation, command.userId, command.scheduleId)

        concertStore.saveSeat(seat)
        reservationStore.saveReservation(reservation)

        return reservationStore.saveReservation(reservation)
    }
}