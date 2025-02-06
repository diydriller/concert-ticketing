package io.hhplus.concert.infrastructure.queue

import io.hhplus.concert.domain.queue.QueueStore
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.util.StringUtil.Companion.ACTIVE_QUEUE_KEY_PREFIX
import io.hhplus.concert.util.StringUtil.Companion.WAITING_QUEUE_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class QueueStoreImpl(
    private val queueTokenRepository: QueueTokenRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) : QueueStore {
    override fun saveQueueToken(queueToken: QueueToken): QueueToken {
        return queueTokenRepository.save(queueToken)
    }

    override fun saveRedisQueueTokenId(tokenId: String) {
        val score = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond().toDouble()
        redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, tokenId, score)
    }

    override fun activateRedisQueueTokenId(tokenId: String) {
        val key = ACTIVE_QUEUE_KEY_PREFIX + tokenId

        redisTemplate.opsForHash<String, String>().put(key, "token", tokenId)
        redisTemplate.expire(key, Duration.ofSeconds(600))
    }

    override fun deleteRedisQueueToken(tokenId: String) {
        val key = ACTIVE_QUEUE_KEY_PREFIX + tokenId
        redisTemplate.delete(key)
    }
}