package com.example.sleuthy.rsocket

import org.springframework.cloud.sleuth.annotation.ContinueSpan
import org.springframework.cloud.sleuth.annotation.NewSpan
import org.springframework.cloud.sleuth.annotation.SpanTag
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Controller
class TaggedTracedController(val service: MyService) {

    @NewSpan("taggedMonoBoolean")
    @MessageMapping("taggedMono")
    fun returnsString(usingUpper: Boolean): Mono<String> = service.caseDependantString(usingUpper)

}

interface MyService {
    fun caseDependantString(useUpper: Boolean): Mono<String>
}

interface MyTracedService : MyService {
    @ContinueSpan(log = "taggedMonoBooleanContinue")
    override fun caseDependantString(@SpanTag("stringIsUpperOrNot") useUpper: Boolean): Mono<String>
}

@Service
class MyServiceImpl : MyTracedService {
    override fun caseDependantString(useUpper: Boolean): Mono<String> =
            Mono.just("service-string")
                    .map {
                        when (useUpper) {
                            true -> it.uppercase()
                            false -> it
                        }
                    }
}