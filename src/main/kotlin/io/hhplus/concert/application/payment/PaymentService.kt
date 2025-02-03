package io.hhplus.concert.application.payment

import io.hhplus.concert.aop.lock.DistributedLock
import io.hhplus.concert.domain.concert.ConcertReader
import io.hhplus.concert.domain.concert.ConcertStore
import io.hhplus.concert.domain.payment.Payment
import io.hhplus.concert.domain.payment.PaymentStore
import io.hhplus.concert.domain.point.PointReader
import io.hhplus.concert.domain.point.PointStore
import io.hhplus.concert.domain.reservation.ReservationDomainService
import io.hhplus.concert.domain.reservation.ReservationReader
import io.hhplus.concert.domain.reservation.ReservationStore
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.response.BaseResponseStatus
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val pointReader: PointReader,
    private val pointStore: PointStore,
    private val reservationReader: ReservationReader,
    private val reservationStore: ReservationStore,
    private val paymentStore: PaymentStore,
    private val concertReader: ConcertReader,
    private val concertStore: ConcertStore,
    private val reservationDomainService: ReservationDomainService
) {
    @Transactional
    fun pay(command: PaymentCommand) {
        val userPoint = pointReader.findPointForUpdate(command.userId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_POINT)
        val reservation = reservationReader.findReservationForUpdate(command.reservationId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_RESERVATION)
        val seat = concertReader.findSeatForUpdate(reservation.seatId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_SEAT)
        val concertSchedule = concertReader.findConcertScheduleForUpdate(reservation.concertScheduleId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT_SCHEDULE)

        reservationDomainService.complete(seat, reservation, concertSchedule)

        userPoint.spend(reservation.price)
        val payment = Payment(
            userId = command.userId,
            reservationId = command.reservationId,
            totalPrice = reservation.price
        )

        concertStore.saveConcertSchedule(concertSchedule)
        concertStore.saveSeat(seat)
        paymentStore.savePayment(payment)
        pointStore.savePoint(userPoint)
        reservationStore.saveReservation(reservation)
    }

    @DistributedLock(key = "payment")
    fun payWithRedissonLock(command: PaymentCommand) {
        val userPoint = pointReader.findPoint(command.userId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_POINT)
        val reservation = reservationReader.findReservation(command.reservationId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_RESERVATION)
        val seat = concertReader.findSeat(reservation.seatId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_SEAT)
        val concertSchedule = concertReader.findConcertSchedule(reservation.concertScheduleId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT_SCHEDULE)

        reservationDomainService.complete(seat, reservation, concertSchedule)

        userPoint.spend(reservation.price)
        val payment = Payment(
            userId = command.userId,
            reservationId = command.reservationId,
            totalPrice = reservation.price
        )

        concertStore.saveConcertSchedule(concertSchedule)
        concertStore.saveSeat(seat)
        paymentStore.savePayment(payment)
        pointStore.savePoint(userPoint)
        reservationStore.saveReservation(reservation)
    }
}