package io.hhplus.concert.application.reservation

import io.hhplus.concert.config.BaseIntegrationTest
import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.infrastructure.concert.ConcertRepository
import io.hhplus.concert.infrastructure.concert.ConcertScheduleRepository
import io.hhplus.concert.infrastructure.concert.SeatRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

@SpringBootTest
class ReservationServiceIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var concertRepository: ConcertRepository

    @Autowired
    private lateinit var reservationService: ReservationService

    @Autowired
    private lateinit var concertScheduleRepository: ConcertScheduleRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Test
    fun `예약함수 호출시 Reservation이 저장되고 반환된다`() {
        // given
        val userId = "0JETAVJVH0SJQ"
        val scheduleId = "0JETAVJVH0SJQ"
        val seatId = "0JETAVJVH0SJQ"
        val concertId = "0JETAVJVH0SJQ"

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
        seatRepository.save(seat)

        // when
        val reservation = reservationService.reserveConcertWithPessimisticLock(
            ReservationCommand(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId
            )
        )

        assertEquals(userId, reservation.userId)
        assertEquals(scheduleId, reservation.concertScheduleId)
        assertEquals(seatId, reservation.seatId)
    }

    @Test
    fun `같은 예약정보로 예약함수 2번 호출시 ConflictException이 발생한다 (비관적락 적용)`() {
        // given
        val userId = "0JETAVJVH0SJK"
        val scheduleId = "0JETAVJVH0SJK"
        val seatId = "0JETAVJVH0SJK"
        val concertId = "0JETAVJVH0SJK"

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
        seatRepository.save(seat)

        // when
        reservationService.reserveConcertWithPessimisticLock(
            ReservationCommand(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId
            )
        )

        assertThrows(ConflictException::class.java) {
            reservationService.reserveConcertWithPessimisticLock(
                ReservationCommand(
                    userId = userId,
                    scheduleId = scheduleId,
                    seatId = seatId
                )
            )
        }
    }

    @Test
    fun `2명이 동시에 같은 좌석 정보로 예약함수를 호출하면 1명만 성공하고 1명은 실패한다 (비관적락 적용)`() {
        // given
        val userIdList = listOf("0JETAVJVH0SJP", "0JETADJVH0SJP")
        val scheduleId = "0JETAVJVH0SJP"
        val seatId = "0JETAVJVH0SJP"
        val concertId = "0JETAVJVH0SJP"

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
        seatRepository.save(seat)

        // when
        val taskCount = 2
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)
        val futureArray = Array(taskCount) { index ->
            CompletableFuture.runAsync {
                try {
                    reservationService.reserveConcertWithPessimisticLock(
                        ReservationCommand(
                            userIdList[index % userIdList.size],
                            scheduleId,
                            seatId
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
    fun `2명이 동시에 같은 좌석 정보로 예약함수를 호출하면 1명만 성공하고 1명은 실패한다 (분산락 적용)`() {
        // given
        val userIdList = listOf("4JETAVJVH0SJP", "5JETADJVH0SJP")
        val scheduleId = "4JETAVJVH0SJP"
        val seatId = "4JETAVJVH0SJP"
        val concertId = "4JETAVJVH0SJP"

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
        seatRepository.save(seat)

        // when
        val taskCount = 2
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)
        val futureArray = Array(taskCount) { index ->
            CompletableFuture.runAsync {
                try {
                    reservationService.reserveConcertWithDistributedLock(
                        ReservationCommand(
                            userIdList[index % userIdList.size],
                            scheduleId,
                            seatId
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