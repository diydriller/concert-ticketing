package io.hhplus.concert.config

import io.hhplus.concert.interceptor.RedisTokenValidationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val redisTokenValidationInterceptor: RedisTokenValidationInterceptor
) : WebMvcConfigurer {
    private val excludedPaths = listOf(
        "/concert",
        "/concert/**",
        "/queue/**",
        "/point/**"
    )

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(redisTokenValidationInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(*excludedPaths.toTypedArray())
    }
}