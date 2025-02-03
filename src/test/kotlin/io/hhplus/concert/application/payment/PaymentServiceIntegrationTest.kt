package io.hhplus.concert.application.payment

import io.hhplus.concert.application.reservation.ReservationCommand
import io.hhplus.concert.application.reservation.ReservationService
import io.hhplus.concert.config.BaseIntegrationTest
import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.domain.point.UserPoint
import io.hhplus.concert.domain.reservation.Reservation
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.infrastructure.concert.ConcertRepository
import io.hhplus.concert.infrastructure.concert.ConcertScheduleRepository
import io.hhplus.concert.infrastructure.concert.SeatRepository
import io.hhplus.concert.infrastructure.point.UserPointRepository
import io.hhplus.concert.infrastructure.reservation.ReservationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
class PaymentServiceIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    @Autowired
    private lateinit var reservationRepository: ReservationRepository

    @Autowired
    private lateinit var concertScheduleRepository: ConcertScheduleRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Autowired
    private lateinit var concertRepository: ConcertRepository


    @Test
    fun `임시 예약을 한 유저가 동시에 2번 결제하면 1번은 성공하고 1번은 실패한다`() {
        // given
        val pointId = "0JETAVJVH0SDF"
        val userId = "0JETAVJVH0SDF"
        val concertId = "0JETAVJVH0SDF"
        val scheduleId = "0JETAVJVH0SDF"
        val seatId = "0JETAVJVH0SDF"
        val reservationId = "0JETAVJVH0SDF"
        val price = 12000

        val userPoint = UserPoint(
            id = pointId,
            userId = userId
        )
        userPoint.point = 300000
        userPointRepository.save(userPoint)


        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )
        concertRepository.save(concert)

        val concertSchedule = ConcertSchedule(
            id = scheduleId,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )
        concertScheduleRepository.save(concertSchedule)

        val seat = Seat(
            id = seatId,
            number = 1,
            price = 12000,
            concertSchedule = concertSchedule
        )
        seat.status = Seat.Status.HOLD
        seat.holdExpiration = LocalDateTime.now().plusMinutes(3)
        seatRepository.save(seat)

        val reservation = Reservation(
            id = reservationId,
            concertScheduleId = scheduleId,
            seatId = seatId,
            userId = userId,
            price = price
        )
        reservation.reserve()
        reservationRepository.save(reservation)

        // when
        val taskCount = 2
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)
        val futureArray = Array(taskCount) {
            CompletableFuture.runAsync {
                try {
                    paymentService.pay(
                        PaymentCommand(
                            userId = userId,
                            reservationId = reservationId
                        )
                    )
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failureCount.incrementAndGet()
                }
            }
        }
        CompletableFuture.allOf(*futureArray).join()

        // then
        Assertions.assertEquals(1, successCount.get())
        Assertions.assertEquals(1, failureCount.get())
    }

    @Test
    fun `잔액이 부족하면 ConflictException이 발생한다`(){
        // given
        val pointId = "0JETAVJVH0SDE"
        val userId = "0JETAVJVH0SDE"
        val concertId = "0JETAVJVH0SDE"
        val scheduleId = "0JETAVJVH0SDE"
        val seatId = "0JETAVJVH0SDE"
        val reservationId = "0JETAVJVH0SDE"
        val price = 12000

        val userPoint = UserPoint(
            id = pointId,
            userId = userId
        )
        userPoint.point = 1000
        userPointRepository.save(userPoint)


        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )
        concertRepository.save(concert)

        val concertSchedule = ConcertSchedule(
            id = scheduleId,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )
        concertScheduleRepository.save(concertSchedule)

        val seat = Seat(
            id = seatId,
            number = 1,
            price = 12000,
            concertSchedule = concertSchedule
        )
        seat.status = Seat.Status.HOLD
        seat.holdExpiration = LocalDateTime.now().plusMinutes(3)
        seatRepository.save(seat)

        val reservation = Reservation(
            id = reservationId,
            concertScheduleId = scheduleId,
            seatId = seatId,
            userId = userId,
            price = price
        )
        reservation.reserve()
        reservationRepository.save(reservation)

        // when & then
        assertThrows(ConflictException::class.java) {
            paymentService.pay(
                PaymentCommand(
                    userId = userId,
                    reservationId = reservationId
                )
            )
        }
    }

    @Test
    fun `분산락 사용해서 임시 예약을 한 유저가 동시에 2번 결제하면 1번은 성공하고 1번은 실패한다`() {
        // given
        val pointId = "0JAAAVJVH0SDF"
        val userId = "0JAAAVJVH0SDF"
        val concertId = "0JAAAVJVH0SDF"
        val scheduleId = "0JAAAVJVH0SDF"
        val seatId = "0JAAAVJVH0SDF"
        val reservationId = "0JAAAVJVH0SDF"
        val price = 12000

        val userPoint = UserPoint(
            id = pointId,
            userId = userId
        )
        userPoint.point = 300000
        userPointRepository.save(userPoint)


        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )
        concertRepository.save(concert)

        val concertSchedule = ConcertSchedule(
            id = scheduleId,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )
        concertScheduleRepository.save(concertSchedule)

        val seat = Seat(
            id = seatId,
            number = 1,
            price = 12000,
            concertSchedule = concertSchedule
        )
        seat.status = Seat.Status.HOLD
        seat.holdExpiration = LocalDateTime.now().plusMinutes(3)
        seatRepository.save(seat)

        val reservation = Reservation(
            id = reservationId,
            concertScheduleId = scheduleId,
            seatId = seatId,
            userId = userId,
            price = price
        )
        reservation.reserve()
        reservationRepository.save(reservation)

        // when
        val taskCount = 2
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)
        val futureArray = Array(taskCount) {
            CompletableFuture.runAsync {
                try {
                    paymentService.payWithRedissonLock(
                        PaymentCommand(
                            userId = userId,
                            reservationId = reservationId
                        )
                    )
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failureCount.incrementAndGet()
                }
            }
        }
        CompletableFuture.allOf(*futureArray).join()

        // then
        Assertions.assertEquals(1, successCount.get())
        Assertions.assertEquals(1, failureCount.get())
    }
}