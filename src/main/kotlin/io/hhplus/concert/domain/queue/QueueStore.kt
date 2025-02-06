package io.hhplus.concert.domain.queue

interface QueueStore {
    fun saveQueueToken(queueToken: QueueToken): QueueToken

    fun saveRedisQueueTokenId(tokenId: String)

    fun activateRedisQueueTokenId(tokenId: String)

    fun deleteRedisQueueToken(tokenId: String)
}