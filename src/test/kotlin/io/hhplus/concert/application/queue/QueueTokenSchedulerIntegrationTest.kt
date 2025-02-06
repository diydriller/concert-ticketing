package io.hhplus.concert.application.queue

import io.hhplus.concert.config.BaseIntegrationTest
import io.hhplus.concert.domain.queue.QueueToken
import io.hhplus.concert.domain.queue.RedisQueueToken
import io.hhplus.concert.infrastructure.queue.QueueTokenRepository
import io.hhplus.concert.util.StringUtil.Companion.ACTIVE_QUEUE_KEY_PREFIX
import io.hhplus.concert.util.StringUtil.Companion.WAITING_QUEUE_KEY
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest
class QueueTokenSchedulerIntegrationTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var queueTokenScheduler: QueueTokenScheduler

    @Autowired
    private lateinit var queueTokenRepository: QueueTokenRepository

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `토큰 활성화시 10개씩 활성화된다 (rdb 사용)`() {
        // given
        val queueTokenIdList: MutableList<String> = mutableListOf()
        repeat(30) {
            val queueToken = QueueToken()
            val savedToken = queueTokenRepository.save(queueToken)
            queueTokenIdList.add(savedToken.id)
        }

        // when
        queueTokenScheduler.activateQueueToken()

        // then
        var activeTokenCount = 0
        queueTokenIdList.forEach { queueTokenId ->
            val savedToken = queueTokenRepository.findQueueTokenById(queueTokenId)
            if (savedToken?.status == QueueToken.Status.ACTIVE) {
                activeTokenCount++
            }
        }
        assertEquals(10, activeTokenCount)
    }

    @Test
    fun `토큰 활성화시 10개씩 활성화된다 (redis 사용)`() {
        // given
        val queueTokenIdList: MutableList<String> = mutableListOf()
        repeat(30) {
            val queueToken = RedisQueueToken()
            val score = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond().toDouble()
            redisTemplate.opsForZSet().add(WAITING_QUEUE_KEY, queueToken.id, score)
            queueTokenIdList.add(queueToken.id)
        }

        // when
        queueTokenScheduler.activateRedisQueueToken()

        // then
        var activeTokenCount = 0
        queueTokenIdList.forEach { queueTokenId ->
            val key = ACTIVE_QUEUE_KEY_PREFIX + queueTokenId
            redisTemplate.opsForHash<String, String>().entries(key)["token"]?.run {
                activeTokenCount++
            }
        }
        assertEquals(10, activeTokenCount)
    }
}