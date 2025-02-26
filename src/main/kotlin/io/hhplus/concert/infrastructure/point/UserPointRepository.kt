package io.hhplus.concert.infrastructure.point

import io.hhplus.concert.domain.point.UserPoint
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface UserPointRepository : JpaRepository<UserPoint, String> {
    @Query("SELECT up FROM UserPoint up WHERE up.userId = :userId ")
    fun findByUserId(userId: String): UserPoint?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT up FROM UserPoint up WHERE up.userId = :userId ")
    fun findByUserIdForUpdate(userId: String): UserPoint?
}