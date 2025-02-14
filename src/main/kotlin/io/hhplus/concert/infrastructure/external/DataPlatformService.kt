package io.hhplus.concert.infrastructure.external

import org.springframework.stereotype.Component

@Component
class DataPlatformService {
    fun sendReservationData(reservationId : String){
        Thread.sleep(30000)
    }
}