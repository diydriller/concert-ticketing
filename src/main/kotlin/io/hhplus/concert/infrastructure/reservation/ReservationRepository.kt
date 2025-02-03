package io.hhplus.concert.infrastructure.reservation

import io.hhplus.concert.domain.reservation.Reservation
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ReservationRepository : JpaRepository<Reservation, String> {
    @Query(
        "SELECT r FROM Reservation r " +
                "WHERE r.userId = :userId AND r.concertScheduleId = :scheduleId AND r.seatId = :seatId AND r.status = :status "
    )
    fun findByUserIdAndScheduleIdAndSeatIdAndStatus(
        userId: String,
        scheduleId: String,
        seatId: String,
        status: Reservation.Status
    ): Reservation?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId ")
    fun findReservationByIdForUpdate(reservationId: String): Reservation?

    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId ")
    fun findReservationById(reservationId: String): Reservation?
}