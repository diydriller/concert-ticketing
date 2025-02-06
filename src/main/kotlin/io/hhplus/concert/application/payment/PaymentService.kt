package io.hhplus.concert.application.payment

import io.hhplus.concert.aop.lock.DistributedLock
import io.hhplus.concert.application.point.PointCommand
import io.hhplus.concert.application.point.PointService
import io.hhplus.concert.application.reservation.ReservationService
import io.hhplus.concert.domain.payment.Payment
import io.hhplus.concert.domain.payment.PaymentStore
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val reservationService: ReservationService,
    private val pointService: PointService,
    private val paymentStore: PaymentStore
) {
    @Transactional
    fun payWithPessimisticLock(command: PaymentCommand) {
        val reservedReservation = reservationService.completeReservationWithPessimisticLock(command.reservationId)

        pointService.usePoint(
            PointCommand.UsePoint(
                userId = command.userId,
                point = reservedReservation.price
            )
        )

        val payment = Payment(
            userId = command.userId,
            reservationId = command.reservationId,
            totalPrice = reservedReservation.price
        )
        paymentStore.savePayment(payment)
    }

    @DistributedLock(key = "payment")
    fun payWithDistributedLock(command: PaymentCommand) {
        val reservedReservation = reservationService.completeReservation(command.reservationId)

        pointService.usePoint(
            PointCommand.UsePoint(
                userId = command.userId,
                point = reservedReservation.price
            )
        )

        val payment = Payment(
            userId = command.userId,
            reservationId = command.reservationId,
            totalPrice = reservedReservation.price
        )
        paymentStore.savePayment(payment)
    }
}