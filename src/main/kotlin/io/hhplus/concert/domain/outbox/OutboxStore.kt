package io.hhplus.concert.domain.outbox

interface OutboxStore {
    fun save(topic: String, message: Any)
}