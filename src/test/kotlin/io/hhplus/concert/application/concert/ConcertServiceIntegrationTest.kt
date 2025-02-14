package io.hhplus.concert.application.concert

import io.hhplus.concert.config.BaseIntegrationTest
import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.infrastructure.concert.ConcertRepository
import io.hhplus.concert.infrastructure.concert.ConcertScheduleRepository
import io.hhplus.concert.infrastructure.concert.SeatRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

@SpringBootTest
class ConcertServiceIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var concertService: ConcertService

    @Autowired
    private lateinit var concertScheduleRepository: ConcertScheduleRepository

    @Autowired
    private lateinit var concertRepository: ConcertRepository

    @Autowired
    private lateinit var seatRepository: SeatRepository

    @Test
    fun `예약 가능 일정 조회시 1개 조회된다 `() {
        // given
        val concertId = "0JETAVJVH0SAA"
        val scheduleId1 = "0JETAVJVH0SAA"
        val scheduleId2 = "0JETAVJVH0SBB"

        val concert = Concert(
            id = concertId,
            name = "검정치마 콘서트"
        )
        concertRepository.save(concert)

        val concertSchedule1 = ConcertSchedule(
            id = scheduleId1,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 18, 0),
            totalSeatCount = 20
        )
        concertSchedule1.reservedSeatCount = 0
        concertScheduleRepository.save(concertSchedule1)
        val concertSchedule2 = ConcertSchedule(
            id = scheduleId2,
            concert = concert,
            date = LocalDateTime.of(2025, 2, 20, 10, 0),
            totalSeatCount = 20
        )
        concertSchedule2.reservedSeatCount = 20
        concertScheduleRepository.save(concertSchedule2)

        // when
        val concertPage = concertService.getConcertSchedule(concertId, 0, 10)

        // then
        assertEquals(1, concertPage.totalElements)
    }

    @Test
    fun `예약 가능 좌석이 2개 조회된다`() {
        // given
        val concertId = "0JETAVJVH0AAA"
        val scheduleId = "0JETAVJVH0AAA"
        val seatId1 = "0JETAVJVH0AAA"
        val seatId2 = "0JETAVJVH0BBB"

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
        concertSchedule.reservedSeatCount = 10
        concertScheduleRepository.save(concertSchedule)

        val seat1 = Seat(
            id = seatId1,
            number = 4,
            price = 12000,
            concertSchedule = concertSchedule
        )
        seatRepository.save(seat1)
        val seat2 = Seat(
            id = seatId2,
            number = 10,
            price = 12000,
            concertSchedule = concertSchedule
        )
        seatRepository.save(seat2)

        // when
        val seatList = concertService.getConcertSeat(concertId, LocalDate.of(2025, 2, 20))

        // then
        assertEquals(2, seatList.size)
    }
}