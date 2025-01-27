package io.hhplus.concert.domain.queue

import com.github.f4b6a3.tsid.TsidCreator
import io.hhplus.concert.domain.BaseModel
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "queue_token")
@Entity
class QueueToken(
    @Id
    val id: String = TsidCreator.getTsid().toString(),
    val userId: String,
    val expiration: LocalDateTime,
) : BaseModel() {
    @Enumerated(EnumType.STRING)
    var status: Status = Status.INACTIVE

    enum class Status {
        ACTIVE, INACTIVE
    }

    fun activate() {
        this.status = Status.ACTIVE
    }

    fun deactivate(){
        this.status = Status.INACTIVE
    }

    fun isValid(): Boolean {
        return status == Status.ACTIVE && expiration.isAfter(LocalDateTime.now())
    }
}