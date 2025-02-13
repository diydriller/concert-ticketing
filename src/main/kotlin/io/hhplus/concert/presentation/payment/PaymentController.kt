package io.hhplus.concert.presentation.payment

import io.hhplus.concert.application.payment.PaymentCommand
import io.hhplus.concert.application.payment.PaymentService
import io.hhplus.concert.response.BaseResponse
import io.hhplus.concert.response.BaseResponseStatus
import org.springframework.web.bind.annotation.*

@RequestMapping("/payment")
@RestController
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping
    fun pay(
        @RequestHeader userId: String,
        @RequestBody request: PaymentRequest.Pay
    ): BaseResponse<Unit> {
        paymentService.payWithPessimisticLock(PaymentCommand(
            userId = userId,
            reservationId = request.reservationId)
        )
        return BaseResponse(BaseResponseStatus.SUCCESS)
    }
}