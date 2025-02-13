package io.hhplus.concert.infrastructure.user

import io.hhplus.concert.domain.user.User
import io.hhplus.concert.domain.user.UserReader
import org.springframework.stereotype.Component

@Component
class UserReaderImpl(
    private val userRepository: UserRepository
) : UserReader {
    override fun findUser(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    override fun isExistUser(userId: String): Boolean {
        return userRepository.existUserById(userId) != null
    }
}