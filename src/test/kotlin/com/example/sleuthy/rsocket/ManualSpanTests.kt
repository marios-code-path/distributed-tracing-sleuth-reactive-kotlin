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
class KafkaManualSpanTests : ManualSpanTests()

@ActiveProfiles("rabbit")
class RabbitMQManualSpanTests : ManualSpanTests()

@ActiveProfiles("rest")
class ZipkinManualSpanTests : ManualSpanTests()

class ManualSpanTests : TestBase() {

    @Test // 1 span ( no client )
    fun `unTraced justMono request`() {
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

    @Test // two spans
    fun `client justMono request`(@Autowired client: SleuthyClient) {

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

    @Test
    fun `ReactorSleuth will propagate hand-built span to justMono`(@Autowired tracer: Tracer) {
        val span = tracer.spanBuilder()
                .kind(Span.Kind.PRODUCER)
                .name("testReactorSleuthSpanBuilder")
                .remoteServiceName("EDDIEVALLIANT")
                .start()

        StepVerifier
                .create(
                        ReactorSleuth.tracedMono(tracer, span) {
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
    fun `trace gets propagated with request`(@Autowired
                                             tracer: Tracer) {
        val manualSpan = tracer.spanBuilder()
                .kind(Span.Kind.CLIENT)
                .name("justMonoRequest")
                .remoteServiceName("EDDIEVALIANT")
                .start()

        StepVerifier.create(
                requester
                        .route("justMono")
                        .retrieveMono<String>()
                        .contextWrite { ctx ->
                            ctx.put(TraceContext::class.java, manualSpan.context())
                        }
                        .doOnError{ thrown ->
                            manualSpan.error(thrown)
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
}