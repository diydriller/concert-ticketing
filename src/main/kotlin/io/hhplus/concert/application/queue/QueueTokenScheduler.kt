package io.hhplus.concert.application.queue

import io.hhplus.concert.domain.queue.QueueReader
import io.hhplus.concert.domain.queue.QueueStore
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class QueueTokenScheduler(
    private val queueReader: QueueReader,
    private val queueStore: QueueStore
) {
    companion object {
        const val ACTIVATE_TOKEN_NUM = 10
    }

    @Transactional
    fun activateQueueToken() {
        queueReader.getWaitingQueueTokenList(ACTIVATE_TOKEN_NUM)
            .forEach { token ->
                token.activate()
                queueStore.saveQueueToken(token)
            }
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    fun activateRedisQueueToken() {
        queueReader.getWaitingRedisQueueTokenIdList(ACTIVATE_TOKEN_NUM)
            .forEach { tokenId ->
                queueStore.activateRedisQueueTokenId(tokenId)
            }
    }
}