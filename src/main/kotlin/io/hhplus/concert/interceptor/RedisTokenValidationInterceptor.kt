package io.hhplus.concert.interceptor

import io.hhplus.concert.application.queue.QueueService
import io.hhplus.concert.response.BaseResponseStatus
import io.hhplus.concert.response.ResponseUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.lang.Exception

@Component
class RedisTokenValidationInterceptor(
    val queueService: QueueService
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val tokenId = request.getHeader("tokenId")
            ?: run {
                ResponseUtil.writeErrorResponse(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    BaseResponseStatus.NOT_FOUND_TOKEN.message
                )
                return false
            }

        if (!queueService.validateRedisQueueToken(tokenId)) {
            ResponseUtil.writeErrorResponse(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                BaseResponseStatus.NOT_VALID_TOKEN.message
            )
            return false
        }
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        @Nullable ex: Exception?
    ) {
        val status = response.status
        if (request.requestURI.startsWith("/payment") && status == 200) {
            val tokenId = request.getHeader("tokenId")
                ?: run {
                    ResponseUtil.writeErrorResponse(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        BaseResponseStatus.NOT_FOUND_TOKEN.message
                    )
                    return
                }

            queueService.deleteRedisQueueToken(tokenId)
        }
    }
}