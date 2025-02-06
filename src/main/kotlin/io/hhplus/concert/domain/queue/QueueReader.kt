package io.hhplus.concert.domain.queue

interface QueueReader {
    fun getQueueToken(tokenId: String): QueueToken?

    fun getQueueTokenOrder(tokenId: String): Int

    fun getWaitingQueueTokenList(num: Int): List<QueueToken>

    fun getRedisQueueTokenOrder(tokenId: String): Int?

    fun getWaitingRedisQueueTokenIdList(num: Int): List<String>

    fun getActiveRedisQueueToken(tokenId: String): String?
}