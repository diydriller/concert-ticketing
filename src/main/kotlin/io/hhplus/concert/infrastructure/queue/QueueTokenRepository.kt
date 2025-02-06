package io.hhplus.concert.infrastructure.queue

import io.hhplus.concert.domain.queue.QueueToken
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QueueTokenRepository : JpaRepository<QueueToken, String> {
    @Query("SELECT qt FROM QueueToken qt WHERE qt.id = :tokenId ")
    fun findQueueTokenById(tokenId: String): QueueToken?

    @Query(
        "SELECT COUNT(qt) FROM QueueToken qt " +
                "WHERE qt.id < :tokenId AND qt.status = 'INACTIVE'"
    )
    fun getQueueTokenOrder(tokenId: String): Int

    @Query(
        "SELECT qt FROM QueueToken qt " +
                "WHERE qt.status = 'INACTIVE'"
    )
    fun getAllInActiveQueueToken(pageable: Pageable): List<QueueToken>
}