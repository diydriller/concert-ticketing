package io.hhplus.concert.application.concert

import io.hhplus.concert.aop.cache.Cache
import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertReader
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.response.BaseResponseStatus
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ConcertService(
    private val concertReader: ConcertReader
) {
    fun getConcertSchedule(concertId: String, page: Int, size: Int): Page<ConcertSchedule> {
        val concert = concertReader.findConcert(concertId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT)
        return concertReader.findReservableConcertSchedule(concert, page, size)
    }

    fun getConcertSeat(concertId: String, date: LocalDate): List<Seat> {
        val concert = concertReader.findConcert(concertId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_CONCERT)
        return concertReader.findReservableSeat(concert, date)
    }

    @Cache(key = "concert")
    fun getConcert(): List<Concert> {
        return concertReader.findConcertList()
    }
}