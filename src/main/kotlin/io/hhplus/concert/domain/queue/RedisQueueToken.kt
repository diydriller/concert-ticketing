package io.hhplus.concert.domain.queue

import com.github.f4b6a3.tsid.TsidCreator

data class RedisQueueToken(
    val id: String = TsidCreator.getTsid().toString(),
)