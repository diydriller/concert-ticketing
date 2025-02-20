package io.hhplus.concert.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun toJson(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun fromJson(json: String, className: String): Any {
        val clazz = Class.forName(className)
        return objectMapper.readValue(json, clazz)
    }
}