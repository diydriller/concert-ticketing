package io.hhplus.concert.application.point

import io.hhplus.concert.domain.point.PointReader
import io.hhplus.concert.domain.point.PointStore
import io.hhplus.concert.domain.point.UserPoint
import io.hhplus.concert.exception.ConflictException
import io.hhplus.concert.exception.NotFoundException
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceUnitTest {
    @InjectMocks
    private lateinit var pointService: PointService

    @Mock
    private lateinit var pointReader: PointReader

    @Mock
    private lateinit var pointStore: PointStore

    @Test
    fun `포인트 사용시 포인트가 없으면 NotFoundExeption이 발생한다`() {
        // given
        val userId = "0JETAVJVH0SJQ"
        val point = 1000

        `when`(pointReader.findPointForUpdate(userId)).then { null }

        // when & then
        assertThrows(NotFoundException::class.java) {
            pointService.usePoint(
                PointCommand.UsePoint(
                    userId,
                    point
                )
            )
        }
    }

    @Test
    fun `포인트 사용시 포인트가 잔고보다 부족하면 ConflictException이 발생한다`() {
        // given
        val userId = "0JETAVJVH0SKK"
        val pointId = "0JETAVJVH0SKK"
        val point = 1000
        val usePoint = 2000

        val userPoint = UserPoint(
            id = pointId,
            userId = userId
        )
        userPoint.point = point

        `when`(pointReader.findPointForUpdate(userId)).then { userPoint }

        // when & then
        assertThrows(ConflictException::class.java) {
            pointService.usePoint(
                PointCommand.UsePoint(
                    userId,
                    usePoint
                )
            )
        }
    }
}