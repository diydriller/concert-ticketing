package io.hhplus.concert.application.payment

import io.hhplus.concert.application.point.PointService
import io.hhplus.concert.application.reservation.ReservationService
import io.hhplus.concert.domain.payment.PaymentStore
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PaymentServiceUnitTest {
    @InjectMocks
    private lateinit var paymentService: PaymentService

    @Mock
    private lateinit var pointService: PointService

    @Mock
    private lateinit var reservationService: ReservationService

    @Mock
    private lateinit var paymentStore: PaymentStore
}