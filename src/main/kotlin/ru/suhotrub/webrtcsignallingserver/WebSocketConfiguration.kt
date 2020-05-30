package ru.suhotrub.webrtcsignallingserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
@EnableWebSocket
class WebSocketConfiguration : WebSocketConfigurer {

    @Autowired
    private lateinit var socketHandler: SocketHandler

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(socketHandler, "/socket").setAllowedOrigins("*")
    }
}

