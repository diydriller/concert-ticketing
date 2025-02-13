package io.hhplus.concert.application.queue

import io.hhplus.concert.domain.queue.QueueReader
import io.hhplus.concert.domain.queue.QueueStore
import io.hhplus.concert.domain.user.UserReader
import io.hhplus.concert.exception.NotFoundException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class QueueServiceUnitTest {
    @InjectMocks
    private lateinit var queueService: QueueService

    @Mock
    private lateinit var queueStore: QueueStore

    @Mock
    private lateinit var queueReader: QueueReader

    @Mock
    private lateinit var userReader: UserReader

    @Test
    fun `토큰 생성시 저장된 유저가 없으면 NotFoundException이 발생한다`() {
        // given
        val userId = "0JETAVJVH0SJQ"

        `when`(userReader.isExistUser(userId)).then { false }

        // when & then
        assertThrows(NotFoundException::class.java) {
            queueService.publishQueueToken(userId)
        }
    }

    @Test
    fun `토큰 조회시 저장된 토큰이 없으면 NotFoundException이 발생한다`() {
        // given
        val tokenId = "0JETAVJVH0SJQ"

        `when`(queueReader.getQueueToken(tokenId)).then { null }

        // when & then
        assertThrows(NotFoundException::class.java) {
            queueService.getWaitingQueueTokenInfo(tokenId)
        }
    }
}
