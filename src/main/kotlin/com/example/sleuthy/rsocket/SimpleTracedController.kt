package com.example.sleuthy.rsocket

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.annotation.ContinueSpan
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.cloud.sleuth.annotation.SpanTag
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import java.time.Duration

@Controller
class SimpleTracedController {

    val log: Logger = LoggerFactory.getLogger(SimpleTracedController::class.java)

    @MessageMapping("justMono")
    fun requestResponse(): Mono<String> = Mono.just("olleh")

    @MessageMapping("justMonoNewSpan")
    @NewSpan("justMonoNewServerSpan")
    fun newSpanRequestResponse(): Mono<String> = Mono.just("olleh")

    @MessageMapping("justFlux")
    fun requestStream(): Flux<String> = Flux.just("s", "l", "e", "u", "t", "h")
            .delayElements(Duration.ofMillis(50))

    @MessageMapping("justFluxNewServerSpan")
    @NewSpan("newSpan")
    fun newSpanRequestStream(): Flux<String> = Flux.just("s", "l", "e", "u", "t", "h")
            .delayElements(Duration.ofMillis(50))
}