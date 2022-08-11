package com.example.sleuthy.rsocket

import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class SleuthyClient() {
    @NewSpan("ClientOriginatedJustMono")
    fun justMono(requester: RSocketRequester): Mono<String> =
            requester
                    .route("justMono")
                    .retrieveMono(String::class.java)

    @NewSpan("ClientOriginatedFlux")
    fun justFlux(requester: RSocketRequester): Flux<String> =
            requester
                    .route("justFlux")
                    .retrieveFlux(String::class.java)
}