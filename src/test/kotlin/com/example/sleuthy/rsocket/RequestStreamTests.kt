package com.example.sleuthy.rsocket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.reactor.ReactorSleuth
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@ActiveProfiles("kafka")
class SleuthyKafkaFluxTests : SleuthyReactorFluxTests()

@ActiveProfiles("rabbit")
class SleuthyRabbitMQFluxTests : SleuthyReactorFluxTests()

class SleuthyReactorFluxTests : TestBase() {

    @Test
    fun `test manual client originated span request stream`(@Autowired
                                                     tracer: Tracer) {
        StepVerifier
                .create(
                        ReactorSleuth.tracedFlux(tracer,
                                tracer.currentTraceContext()!!, "tracedFluxClientOriginated") {
                            requester
                                    .route("justFlux")
                                    .retrieveFlux(String::class.java)
                        }
                )
                .thenConsumeWhile {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .containsAnyOf("s", "l", "e", "u", "t", "h")
                    true
                }
                .verifyComplete()
    }

    @Test
    fun `client stream test`(@Autowired
                             client: SleuthyClient) {

        StepVerifier.create(
                client.justFlux(requester)
        )
                .thenConsumeWhile {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .containsAnyOf("s", "l", "e", "u", "t", "h")
                    true
                }
                .verifyComplete()
    }
}