package io.hhplus.concert.infrastructure.outbox

import io.hhplus.concert.domain.outbox.Outbox
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OutboxRepository : JpaRepository<Outbox, String> {
    @Query("SELECT o FROM Outbox o WHERE o.status = :status ")
    fun findAllByStatus(status: Outbox.OutboxStatus): List<Outbox>
}