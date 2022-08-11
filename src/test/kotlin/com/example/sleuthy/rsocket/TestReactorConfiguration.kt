package com.example.sleuthy.rsocket

import io.rsocket.core.RSocketServer
import io.rsocket.transport.netty.server.CloseableChannel
import io.rsocket.transport.netty.server.TcpServerTransport
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler


@Profile("no-trace-shipping")
@TestConfiguration
class TestReactorConfiguration() {
    @Value("\${spring.rsocket.server.port:0}")
    lateinit var serverPort: String

    @Bean
    fun rSocketServer(handler: RSocketMessageHandler): RSocketServer = RSocketServer
            .create(handler.responder())

    @Bean
    fun rSocketConnectedServer(rs: RSocketServer): CloseableChannel =
            rs.bind(TcpServerTransport.create("localhost", serverPort.toInt())).block()!!


    @Bean
    fun rSocketRequester(server: CloseableChannel, strategies: RSocketStrategies): RSocketRequester = RSocketRequester
            .builder()
            .rsocketStrategies(strategies)
            .connectTcp("localhost", server.address().port)
            .block()!!

    @Bean
    fun requesterBuilder(): RSocketRequester.Builder =
            RSocketRequester.builder()

}