package io.hhplus.concert.aop.cache

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.random.Random

@Aspect
@Component
class CacheAop(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    @Around("@annotation(io.hhplus.concert.aop.cache.Cache)")
    fun cacheAround(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val cache = method.getAnnotation(Cache::class.java)
        val cacheKey = cache.key

        redisTemplate.opsForValue().get(cacheKey)?.let { json ->
            return json
        }

        val result = joinPoint.proceed()

        val ttl = cache.defaultTtl + Random.nextLong(0, 60)
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofSeconds(ttl))

        return result
    }
}