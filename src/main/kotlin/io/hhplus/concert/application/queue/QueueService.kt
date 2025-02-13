package io.hhplus.concert.application.queue

import io.hhplus.concert.domain.queue.QueueReader
import io.hhplus.concert.domain.queue.QueueStore
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.domain.queue.RedisQueueToken
import io.hhplus.concert.domain.user.UserReader
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.response.BaseResponseStatus
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val queueStore: QueueStore,
    private val queueReader: QueueReader,
    private val userReader: UserReader
) {
    fun publishQueueToken(userId: String): QueueToken {
        if(!userReader.isExistUser(userId)) {
            throw NotFoundException(BaseResponseStatus.NOT_FOUND_USER)
        }
        val queueToken = QueueToken()
        return queueStore.saveQueueToken(queueToken)
    }

    fun getWaitingQueueTokenInfo(tokenId: String): QueueTokenOrderInfo {
        val queueToken = queueReader.getQueueToken(tokenId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_TOKEN)
        val order = queueReader.getQueueTokenOrder(tokenId)
        return QueueTokenOrderInfo(
            id = queueToken.id,
            order = order
        )
    }

    fun publishRedisQueueToken(userId: String): RedisQueueToken {
        if(!userReader.isExistUser(userId)) {
            throw NotFoundException(BaseResponseStatus.NOT_FOUND_USER)
        }
        val redisQueueToken = RedisQueueToken()
        queueStore.saveRedisQueueTokenId(redisQueueToken.id)
        return redisQueueToken
    }

    fun getWaitingRedisQueueTokenInfo(tokenId: String): QueueTokenOrderInfo {
        val order = queueReader.getRedisQueueTokenOrder(tokenId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_TOKEN)
        return QueueTokenOrderInfo(
            id = tokenId,
            order = order
        )
    }

    fun validateRedisQueueToken(tokenId: String): Boolean {
        return queueReader.getActiveRedisQueueToken(tokenId) != null
    }

    fun deleteRedisQueueToken(tokenId: String){
        queueStore.deleteRedisQueueToken(tokenId)
    }
}