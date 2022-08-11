package com.example.sleuthy.rsocket

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.util.retry.Retry
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class TestBase {

    lateinit var requester: RSocketRequester

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @BeforeEach
    fun setUp(@Autowired
              builder: RSocketRequester.Builder,
              @Value("\${spring.rsocket.server.port:0}")
              serverPort: String
    ) {
        requester = builder.
                rsocketConnector{ connector ->
                    connector.reconnect(Retry.fixedDelay(3, Duration.ofSeconds(5))
                            .doAfterRetry { sig -> log.warn("retried $sig") })
                }.tcp("localhost", serverPort.toInt())
    }

    @Test
    fun contextLoads() {
    }
}