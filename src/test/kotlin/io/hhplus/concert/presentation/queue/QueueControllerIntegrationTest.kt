package io.hhplus.concert.presentation.queue

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.concert.config.BaseIntegrationTest
import io.hhplus.concert.domain.concert.Concert
import io.hhplus.concert.domain.concert.ConcertSchedule
import io.hhplus.concert.domain.concert.Seat
import io.hhplus.concert.domain.point.UserPoint
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.domain.queue.RedisQueueToken
import io.hhplus.concert.domain.reservation.Reservation
import io.hhplus.concert.infrastructure.concert.ConcertRepository
import io.hhplus.concert.infrastructure.concert.ConcertScheduleRepository
import io.hhplus.concert.infrastructure.concert.SeatRepository
import io.hhplus.concert.infrastructure.point.UserPointRepository
import io.hhplus.concert.infrastructure.queue.QueueTokenRepository
import io.hhplus.concert.infrastructure.reservation.ReservationRepository
import io.hhplus.concert.presentation.payment.PaymentRequest
import io.hhplus.concert.util.StringUtil.Companion.ACTIVE_QUEUE_KEY_PREFIX
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Duration
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest
class QueueControllerIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `결제까지 완료하면 대기열 토큰은 비활성화된다 (redis 사용)`() {
        // given
        val tokenId = "0JETAVJVH0SVV"
        val pointId = "0JETAVJVH0SVV"
        val userId = "0JETAVJVH0SVV"
        val concertId = "0JETAVJVH0SVV"
        val scheduleId = "0JETAVJVH0SVV"
        val seatId = "0JETAVJVH0SVV"
        val reservationId = "0JETAVJVH0SVV"
        val price = 12000

        val key = ACTIVE_QUEUE_KEY_PREFIX + tokenId
        redisTemplate.opsForHash<String, String>().put(key, "token", tokenId)
        redisTemplate.expire(key, Duration.ofSeconds(600))

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

        val paymentRequest = PaymentRequest.Pay(
            reservationId = reservationId
        )
        val requestJson = objectMapper.writeValueAsString(paymentRequest)

        // when
        mockMvc.perform(
            post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
                .header("userId", userId)
                .header("tokenId", tokenId)
        )
            .andExpect(status().isOk)

        //then
        val exist = redisTemplate.opsForHash<String, String>().entries(key)["token"] != null

        assertEquals(exist, false)
    }

}