package ru.suhotrub.webrtcsignallingserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList


@Component
class SocketHandler : TextWebSocketHandler() {

    private val sessions: MutableList<WebSocketSession> by lazy {
        CopyOnWriteArrayList<WebSocketSession>()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val objectMapper = ObjectMapper()
            val jsonNode = objectMapper.readTree(message.payload) as ObjectNode

            val rawSessionId = jsonNode["sessionId"]!!.asText()
            jsonNode.put("sessionId", session.id)

            val newMessage = TextMessage(objectMapper.writeValueAsString(jsonNode))
            rawSessionId.takeIf { it.isNotEmpty() }?.let { sessionId ->
                sessions.find { it.id == sessionId }?.sendMessage(newMessage)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        sendMembers()
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.removeIf { it.id == session.id }
        sendMembers()
    }

    private fun sendMembers() {
        sessions.removeIf { !it.isOpen }

        val mapper = ObjectMapper()
        sessions.forEach { session ->
            try {
                val message = mapper.writeValueAsString(mutableMapOf(
                        "members" to sessions.filter { it.id != session.id }.map { it.id }
                ))
                session.sendMessage(TextMessage(message))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}




