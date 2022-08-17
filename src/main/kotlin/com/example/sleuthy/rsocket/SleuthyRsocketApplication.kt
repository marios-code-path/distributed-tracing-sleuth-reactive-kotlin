package com.example.sleuthy.rsocket

import brave.sampler.Sampler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SleuthyRsocketApplication {
    @Bean
    fun sleuthTraceSampler(): Sampler? {
        return Sampler.ALWAYS_SAMPLE
    }
}

fun main(args: Array<String>) {
    runApplication<SleuthyRsocketApplication>(*args)
}

