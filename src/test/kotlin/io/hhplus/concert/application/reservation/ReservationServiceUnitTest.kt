package io.hhplus.concert.application.reservation

import io.hhplus.concert.domain.concert.*
import io.hhplus.concert.domain.reservation.Reservation
import io.hhplus.concert.domain.reservation.ReservationDomainService
import io.hhplus.concert.domain.reservation.ReservationStore
import io.hhplus.concert.exception.NotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ReservationServiceUnitTest {
    @InjectMocks
    private lateinit var reservationService: ReservationService

    @Mock
    private lateinit var reservationStore: ReservationStore

    @Mock
    private lateinit var concertReader: ConcertReader

    @Mock
    private lateinit var concertStore: ConcertStore

    @Mock
    private lateinit var reservationDomainService: ReservationDomainService

    @Test
    fun `예약 함수 호출시 콘서트 스케줄이 존재하지 않으면 NotFoundException이 발생한다`() {
        // given
        val userId = "0JETAVJVH0SJQ"
        val scheduleId = "0JETAVJVH0SJQ"
        val seatId = "0JETAVJVH0SJQ"

        `when`(concertReader.findConcertSchedule(scheduleId)).then { null }

        // when & then
        assertThrows(NotFoundException::class.java) {
            reservationService.reserveConcert(
                ReservationCommand(
                    userId = userId,
                    scheduleId = scheduleId,
                    seatId = seatId
                )
            )
        }
    }

    @Test
    fun `예약 함수 호출시 좌석이 존재하지 않으면 NotFoundException이 발생한다`() {
        // given
        val userId = "0JETAVJVH0SJQ"
        val scheduleId = "0JETAVJVH0SJQ"
        val seatId = "0JETAVJVH0SJQ"
        val concertId = "0JETAVJVH0SJQ"

        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )

        val concertSchedule = ConcertSchedule(
            id = scheduleId,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )

        `when`(concertReader.findConcertSchedule(scheduleId)).then { concertSchedule }

        `when`(concertReader.findSeatForUpdate(seatId)).then { null }

        // when & then
        assertThrows(NotFoundException::class.java) {
            reservationService.reserveConcert(
                ReservationCommand(
                    userId = userId,
                    scheduleId = scheduleId,
                    seatId = seatId
                )
            )
        }
    }

    @Test
    fun `예약함수가 정상적으로 호출시 예약 정보가 알맞은 Reservation을 반환한다`() {
        // given
        val userId = "0JETAVJVH0SJQ"
        val scheduleId = "0JETAVJVH0SJQ"
        val seatId = "0JETAVJVH0SJQ"
        val concertId = "0JETAVJVH0SJQ"
        val reservationId = "0JETAVJVH0SJQ"
        val price = 12000

        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )

        val concertSchedule = ConcertSchedule(
            id = scheduleId,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )

        val seat = Seat(
            id = seatId,
            number = 1,
            price = price,
            concertSchedule = concertSchedule
        )

        val reservation = Reservation(
            id = reservationId,
            concertScheduleId = scheduleId,
            seatId = seatId,
            userId = userId,
            price = price
        )

        `when`(concertReader.findConcertSchedule(scheduleId)).then { concertSchedule }

        `when`(concertReader.findSeatForUpdate(seatId)).then { seat }

        `when`(reservationStore.saveReservation(any())).then { reservation }

        // when & then
        val savedReservation = reservationService.reserveConcert(
            ReservationCommand(
                userId = userId,
                scheduleId = scheduleId,
                seatId = seatId
            )
        )

        assertEquals(userId, savedReservation.userId)
        assertEquals(seatId, savedReservation.seatId)
        assertEquals(scheduleId, savedReservation.concertScheduleId)
    }
}