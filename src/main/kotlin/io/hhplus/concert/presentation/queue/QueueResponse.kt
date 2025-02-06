package io.hhplus.concert.presentation.queue

import io.hhplus.concert.application.queue.QueueTokenOrderInfo
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.domain.queue.RedisQueueToken

class QueueResponse {
    data class GetQueueTokenInfo(
        val id: String
    ) {
        companion object {
            fun fromQueueToken(queueToken: QueueToken): GetQueueTokenInfo {
                return GetQueueTokenInfo(
                    id = queueToken.id
                )
            }

            fun fromRedisQueueToken(redisQueueToken: RedisQueueToken): GetQueueTokenInfo {
                return GetQueueTokenInfo(
                    id = redisQueueToken.id
                )
            }
        }
    }

    data class GetQueueTokenOrderInfo(
        val id: String,
        val order: Int
    ) {
        companion object {
            fun fromQueueTokenInfo(queueTokenOrderInfo: QueueTokenOrderInfo): GetQueueTokenOrderInfo {
                return GetQueueTokenOrderInfo(
                    id = queueTokenOrderInfo.id,
                    order = queueTokenOrderInfo.order
                )
            }
        }
    }
}