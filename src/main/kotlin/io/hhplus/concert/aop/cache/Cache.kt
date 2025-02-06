package io.hhplus.concert.aop.cache

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cache(
    val key: String,
    val defaultTtl: Long = 200,
)
