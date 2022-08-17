package com.example.sleuthy.rsocket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.annotation.ContinueSpan
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.messaging.rsocket.retrieveMono
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class SleuthyClient() {

    val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var tracer: Tracer

    @NewSpan("ClientOriginated")
    fun justMono(requester: RSocketRequester): Mono<String> =
            requester
                    .route("justMono")
                    .retrieveMono<String>()
                    .doOnNext {
                        log.info("justMono: ${tracer.currentSpan()?.toString()}")
                    }

    @NewSpan("clientJustMonoNewSpan")
    fun justMonoNewSpan(requester: RSocketRequester): Mono<String> =
            requester
                    .route("justMonoNewSpan")
                    .retrieveMono<String>()

    @NewSpan("ClientOriginatedFlux")
    fun justFlux(requester: RSocketRequester): Flux<String> =
            requester
                    .route("justFlux")
                    .retrieveFlux<String>()

    @ContinueSpan
    fun coRoute(requester: RSocketRequester): Mono<String> =
            requester
                    .route("coroute")
                    .retrieveMono<String>()

    @NewSpan("ClientCoRouteNewSpan")
    fun coRouteNewSpan(requester: RSocketRequester): Mono<String> =
            requester
                    .route("corouteNewSpan")
                    .retrieveMono<String>()
}