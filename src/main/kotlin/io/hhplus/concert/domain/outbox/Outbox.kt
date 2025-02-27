package io.hhplus.concert.domain.outbox

import com.github.f4b6a3.tsid.TsidCreator
import io.hhplus.concert.domain.BaseModel
import jakarta.persistence.*

@Table(name = "outbox")
@Entity
class Outbox(
    val topic: String,

    val payload: String
) : BaseModel() {
    @Id
    val id: String = TsidCreator.getTsid().toString()

    @Enumerated(EnumType.STRING)
    var status: OutboxStatus = OutboxStatus.PENDING

    enum class OutboxStatus {
        PENDING, FAILED
    }
}