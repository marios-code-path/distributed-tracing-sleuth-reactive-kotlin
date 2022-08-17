package com.example.sleuthy.rsocket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.TraceContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@ActiveProfiles("kafka")
class KafkaTaggedRequestResponseTests : TaggedRequestResponseTests()

class TaggedRequestResponseTests : TestBase() {

    @Test
    fun `test taggedMono`(@Autowired
                          tracer: Tracer) {
        val manualSpan = tracer.spanBuilder()
                .kind(Span.Kind.CLIENT)
                .name("tracerTaggedClient")
                .remoteServiceName("EDDIEVALIANT")
                .tag("client", "test")
                .start()

        StepVerifier.create(
                requester
                        .route("taggedMono")
                        .data(true)
                        .retrieveMono<String>()
                        .contextWrite { ctx ->
                            ctx.put(TraceContext::class.java, manualSpan.context())
                        }
                        .doOnError { thrown ->
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
                            .contains("SERVICE-STRING")
                }
                .verifyComplete()
    }
}