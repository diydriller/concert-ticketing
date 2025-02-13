package io.hhplus.concert.infrastructure.queue

import com.fasterxml.jackson.databind.ObjectMapper
import io.hhplus.concert.domain.queue.QueueReader
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.domain.queue.RedisQueueToken
import io.hhplus.concert.util.StringUtil.Companion.ACTIVE_QUEUE_KEY_PREFIX
import io.hhplus.concert.util.StringUtil.Companion.WAITING_QUEUE_KEY
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class QueueReaderImpl(
    private val queueTokenRepository: QueueTokenRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) : QueueReader {
    override fun getQueueToken(tokenId: String): QueueToken? {
        return queueTokenRepository.findQueueTokenById(tokenId)
    }

    override fun getQueueTokenOrder(tokenId: String): Int {
        return queueTokenRepository.getQueueTokenOrder(tokenId)
    }

    override fun getWaitingQueueTokenList(num: Int): List<QueueToken> {
        val pageable = PageRequest.of(0, num, Sort.by("id").ascending())
        return queueTokenRepository.getAllInActiveQueueToken(pageable)
    }

    override fun getRedisQueueTokenOrder(tokenId: String): Int? {
        return redisTemplate.opsForZSet().rank(WAITING_QUEUE_KEY, tokenId)?.toInt()
    }

    override fun getWaitingRedisQueueTokenIdList(num: Int): List<String> {
        val tokenIdList = redisTemplate.opsForZSet().popMin(WAITING_QUEUE_KEY, num.toLong()) ?: return emptyList()
        return tokenIdList.map { it.value!! }
    }

    override fun getActiveRedisQueueToken(tokenId: String): String? {
        val key = ACTIVE_QUEUE_KEY_PREFIX + tokenId
        return redisTemplate.opsForHash<String, String>().entries(key)["token"]
    }
}