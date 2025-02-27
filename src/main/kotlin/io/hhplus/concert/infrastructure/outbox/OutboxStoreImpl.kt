package io.hhplus.concert.infrastructure.outbox

import io.hhplus.concert.domain.outbox.Outbox
import io.hhplus.concert.domain.outbox.OutboxStore
import io.hhplus.concert.util.JsonUtil.toJson
import org.springframework.stereotype.Component

@Component
class OutboxStoreImpl(
    private val outboxRepository: OutboxRepository
) : OutboxStore {
    override fun save(topic: String, message: Any) {
        val payload = toJson(message)
        val outbox = Outbox(topic, payload)
        outboxRepository.save(outbox)
    }
}