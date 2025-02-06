package io.hhplus.concert.application.point

class PointCommand {
    data class GetPoint(
        val userId: String
    )

    data class ChargePoint(
        val userId: String,
        val point: Int
    )

    data class UsePoint(
        val userId: String,
        val point: Int
    )
}
