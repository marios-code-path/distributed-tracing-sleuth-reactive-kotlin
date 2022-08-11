package com.example.sleuthy.rsocket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.reactor.ReactorSleuth
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@ActiveProfiles("kafka")
class KafkaRequestResponseTests : RequestResponseTests()

@ActiveProfiles("rabbit")
class RabbitMQRequestResponseTests : RequestResponseTests()

@ActiveProfiles("rest")
class ZipkinRequestResponseTests : RequestResponseTests()

class RequestResponseTests : TestBase() {

    @Test
    fun `non request-traced request`() {
        val requester = RSocketRequester.builder().tcp("localhost", 10001)

        StepVerifier.create(
                requester
                        .route("justMono")
                        .retrieveMono<String>()
        )
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("olleh")
                }
                .verifyComplete()
    }

    @Test
    fun `manual span bearing request`(@Autowired
                                      tracer: Tracer) {
        val manualSpan = tracer.spanBuilder()
                .kind(Span.Kind.CLIENT)
                .name("tracerJustMonoRequest")
                .remoteServiceName("EDDIEVALLIANT")
                .start()

        StepVerifier.create(
                requester
                        .route("justMono")
                        .retrieveMono<String>()
                        .contextWrite { ctx ->
                            ctx.put(TraceContext::class.java, manualSpan.context())
                        }
                        .doFinally { sig ->
                            manualSpan.end()
                        }

        )
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("olleh")
                }
                .verifyComplete()
    }

    @Test
    fun `reactorSleuth span bearing request`(@Autowired
                                             tracer: Tracer) {
        StepVerifier
                .create(
                        ReactorSleuth.tracedMono(tracer,
                                tracer.currentTraceContext()!!, "reactorSleuthJustMonoRequest") {
                            requester
                                    .route("justMono")
                                    .retrieveMono<String>()
                        }
                )
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("olleh")
                }
                .verifyComplete()
    }

    @Test
    fun `client originated request`(@Autowired client: SleuthyClient) {
        StepVerifier
                .create(client.justMono(requester))
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("olleh")
                }
                .verifyComplete()
    }
}