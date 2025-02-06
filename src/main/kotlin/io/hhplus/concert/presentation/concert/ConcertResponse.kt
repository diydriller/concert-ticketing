package io.hhplus.concert.presentation.concert

import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat

class ConcertResponse {
    data class GetConcertScheduleInfo(
        val id: String,
        val date: String
    ) {
        companion object {
            fun fromEntity(concertSchedule: ConcertSchedule): GetConcertScheduleInfo {
                return GetConcertScheduleInfo(
                    concertSchedule.id,
                    concertSchedule.date.toString()
                )
            }
        }
    }

    data class GetSeatInfo(
        val id: String,
        val number: Int,
        val price: Int
    ) {
        companion object {
            fun fromEntity(seat: Seat): GetSeatInfo {
                return GetSeatInfo(
                    id = seat.id,
                    number = seat.number,
                    price = seat.price
                )
            }
        }
    }

    data class GetConcertInfo(
        val id: String,
        val name: String
    ) {
        companion object {
            fun fromEntity(concert: Concert): GetConcertInfo {
                return GetConcertInfo(
                    id = concert.id,
                    name = concert.name
                )
            }
        }
    }
}