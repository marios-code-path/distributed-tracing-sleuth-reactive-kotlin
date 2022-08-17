package com.example.sleuthy.rsocket

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

class AnnotatedSpanTests : TestBase() {

    @Test
    fun `client propagates trace to justMono endpoint`(@Autowired client: SleuthyClient) {
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
    fun `test client coRouteNewSpan request`(@Autowired client: SleuthyClient) {
        StepVerifier
                .create(client.coRouteNewSpan(requester))
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("Olleh")
                }
                .verifyComplete()
    }

    @Test
    fun `test client justMonoNewSpan request`(@Autowired client: SleuthyClient) {
        val publisher = client.justMonoNewSpan(requester)
        StepVerifier
                .create(publisher)
                .assertNext {
                    Assertions
                            .assertThat(it)
                            .isNotNull
                            .contains("olleh")
                }
                .verifyComplete()
    }


}