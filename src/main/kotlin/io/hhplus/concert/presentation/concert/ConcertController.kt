package io.hhplus.concert.presentation.concert

import io.hhplus.concert.application.concert.ConcertService
import io.hhplus.concert.response.BaseResponse
import io.hhplus.concert.response.PageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RequestMapping("/concert")
@RestController
class ConcertController(
    private val concertService: ConcertService
) {
    @GetMapping
    fun getConcert(): ResponseEntity<List<ConcertResponse.GetConcertInfo>>{
        val concertList = concertService.getConcert()

        val concertResponse = concertList.map { concert ->
            ConcertResponse.GetConcertInfo.fromEntity(concert)
        }.toList()

        return ResponseEntity.ok().body(
            concertResponse
        )
    }

    @GetMapping("/{concertId}/schedule")
    fun getConcertSchedule(
        @PathVariable concertId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<ConcertResponse.GetConcertScheduleInfo>> {
        val concertSchedulePage = concertService.getConcertSchedule(concertId, page, size)

        val concertScheduleList = concertSchedulePage.map { concertSchedule ->
            ConcertResponse.GetConcertScheduleInfo.fromEntity(concertSchedule)
        }.toList()

        return ResponseEntity.ok().body(
            PageResponse(
                data = concertScheduleList,
                totalPage = concertSchedulePage.totalPages,
                totalElements = concertSchedulePage.totalElements,
                page = page,
                size = size
            )
        )
    }

    @GetMapping("/{concertId}/schedule/{scheduleId}/seat")
    fun getConcertSeat(
        @PathVariable concertId: String,
        @PathVariable scheduleId: String,
        @RequestParam date: String
    ): ResponseEntity<BaseResponse<List<ConcertResponse.GetSeatInfo>>> {
        val seatResponse = concertService.getConcertSeat(concertId, scheduleId, LocalDate.parse(date))
            .map { seat ->
                ConcertResponse.GetSeatInfo.fromEntity(seat)
            }.toList()

        return ResponseEntity.ok().body(
            BaseResponse(seatResponse)
        )
    }
}