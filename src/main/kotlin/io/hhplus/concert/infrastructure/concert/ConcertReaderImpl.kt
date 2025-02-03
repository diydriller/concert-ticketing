package io.hhplus.concert.infrastructure.concert

import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertReader
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ConcertReaderImpl(
    private val concertScheduleRepository: ConcertScheduleRepository,
    private val concertRepository: ConcertRepository,
    private val seatRepository: SeatRepository
) : ConcertReader {
    override fun findConcert(concertId: String): Concert? {
        return concertRepository.findConcertById(concertId)
    }

    override fun findReservableConcertSchedule(concert: Concert, page: Int, size: Int): Page<ConcertSchedule> {
        val pageable = PageRequest.of(page, size, Sort.by("createdDate").descending())
        return concertScheduleRepository.findReservableConcertSchedule(concert, pageable)
    }

    override fun findConcertSchedule(scheduleId: String): ConcertSchedule? {
        return concertScheduleRepository.findConcertScheduleById(scheduleId)
    }

    override fun findReservableSeat(schedule: ConcertSchedule, date: LocalDate): List<Seat> {
        return seatRepository.findReservableSeat(schedule, date)
    }

    override fun findSeatForUpdate(seatId: String): Seat? {
        return seatRepository.findSeatByIdForUpdate(seatId)
    }

    override fun findSeat(seatId: String): Seat? {
        return seatRepository.findSeatById(seatId)
    }

    override fun findConcertScheduleForUpdate(scheduleId: String): ConcertSchedule? {
        return concertScheduleRepository.findConcertScheduleByIdForUpdate(scheduleId)
    }
}