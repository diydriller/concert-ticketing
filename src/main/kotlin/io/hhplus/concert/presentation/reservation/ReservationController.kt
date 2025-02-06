package io.hhplus.concert.presentation.reservation

import io.hhplus.concert.application.reservation.ReservationService
import io.hhplus.concert.response.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/reservation")
@RestController
class ReservationController(
    private val reservationService: ReservationService
) {
    @PostMapping
    fun reserveConcert(
        @RequestBody request: ReservationRequest.ReserveConcert,
        @RequestHeader userId: String
    ): ResponseEntity<BaseResponse<ReservationResponse.ReserveConcert>> {
        val reservation = reservationService.reserveConcertWithPessimisticLock(request.toCommand(userId))

        return ResponseEntity.status(HttpStatus.CREATED).body(
            BaseResponse(
                ReservationResponse.ReserveConcert.fromEntity(reservation)
            )
        )
    }
}