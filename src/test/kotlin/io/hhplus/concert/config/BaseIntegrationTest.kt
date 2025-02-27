package io.hhplus.concert.config

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class BaseIntegrationTest {
    companion object {
        @Container
        private val mysqlContainer = MySQLContainer<Nothing>("mysql:8.0.35").apply {
            withDatabaseName("testdb")
            withUsername("root")
            withPassword("rootpass")
        }

        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("storage.datasource.core.jdbc-url") { mysqlContainer.jdbcUrl }
            registry.add("storage.datasource.core.username") { mysqlContainer.username }
            registry.add("storage.datasource.core.password") { mysqlContainer.password }
        }

        @Container
        private val redisContainer = GenericContainer(DockerImageName.parse("redis:6.2.5")).apply {
            withExposedPorts(6379)
        }

        @BeforeAll
        @JvmStatic
        fun startRedis() {
            redisContainer.start()
            System.setProperty("redis.host", redisContainer.host)
            System.setProperty("redis.port", redisContainer.getMappedPort(6379).toString())
        }

        @AfterAll
        @JvmStatic
        fun stopRedis() {
            redisContainer.stop()
        }

        @Container
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"))

        @BeforeAll
        @JvmStatic
        fun startKafka() {
            kafkaContainer.start()
            System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.bootstrapServers)
        }

        @AfterAll
        @JvmStatic
        fun stopKafka() {
            kafkaContainer.stop()
        }
    }
}