package io.hhplus.concert.infrastructure.user

import io.hhplus.concert.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    fun findUserById(userId: String): User?

    @Query("SELECT u.id FROM User u WHERE u.id = :userId ")
    fun existUserById(userId: String): String?
}