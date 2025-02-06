package io.hhplus.concert.application.point

import io.hhplus.concert.domain.point.PointReader
import io.hhplus.concert.domain.point.PointStore
import io.hhplus.concert.domain.point.UserPoint
import io.hhplus.concert.exception.NotFoundException
import io.hhplus.concert.response.BaseResponseStatus
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointReader: PointReader,
    private val pointStore: PointStore
) {
    fun getPoint(command: PointCommand.GetPoint): UserPoint {
        return pointReader.findPoint(command.userId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_POINT)
    }

    fun chargePoint(command: PointCommand.ChargePoint): UserPoint {
        val userPoint = pointReader.findPoint(command.userId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_POINT)
        userPoint.charge(command.point)
        return userPoint
    }

    fun usePoint(command: PointCommand.UsePoint): UserPoint{
        val userPoint = pointReader.findPointForUpdate(command.userId)
            ?: throw NotFoundException(BaseResponseStatus.NOT_FOUND_POINT)

        userPoint.spend(command.point)

        return pointStore.savePoint(userPoint)
    }
}