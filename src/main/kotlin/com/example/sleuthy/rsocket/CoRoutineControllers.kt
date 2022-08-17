package com.example.sleuthy.rsocket

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.cloud.sleuth.annotation.ContinueSpan
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class CoRoutineControllers {

    @MessageMapping("coroute")
    @ContinueSpan
    suspend fun coMono(): String = Mono.just("Olleh").awaitSingle()

    @MessageMapping("corouteNewSpan")
    @NewSpan("coRoutineSpan")
    suspend fun coMonoSpan(): String = Mono.just("Olleh").awaitSingle()
}

