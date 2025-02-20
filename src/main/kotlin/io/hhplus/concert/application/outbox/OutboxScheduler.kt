package io.hhplus.concert.application.outbox

import io.hhplus.concert.domain.outbox.Outbox
import io.hhplus.concert.infrastructure.outbox.OutboxRepository
import jakarta.transaction.Transactional
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxScheduler(
    private val outboxRepository: OutboxRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    @Scheduled(fixedRate = 30000)
    @Transactional
    fun publishOutbox() {
        outboxRepository.findAllByStatus(Outbox.OutboxStatus.PENDING).forEach { outbox ->
            kafkaTemplate.send(outbox.topic, outbox.payload).whenComplete { _, exception ->
                if (exception == null) {
                    outboxRepository.delete(outbox)
                } else {
                    outbox.status = Outbox.OutboxStatus.FAILED
                    outboxRepository.save(outbox)
                }
            }
        }
    }
}