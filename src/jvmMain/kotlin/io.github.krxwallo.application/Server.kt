package io.github.krxwallo.application

import GameEntity
import GameInstance
import io.github.krxwallo.synk.Synk
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scope
import java.time.Duration
import java.util.*

fun main() {
    embeddedServer(Netty, port = 8081, host = "127.0.0.1", module = Application::myApplicationModule).start(wait = true)
}

fun Application.myApplicationModule() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val instance = GameInstance()
    scope.launch {
        while (true) {
            instance.intProperty++ // is synced to clients
            delay(3000)
        }
    }

    Synk.handleServerSend { instance1, data ->
        when (instance1) {
            is GameInstance -> {
                scope.launch {
                    instance1.players.forEach {
                        try {
                            it.connection?.send(data)
                        } catch (e: Exception) {
                            println("Failed to send data to client with uuid ${it.uuid}: $e")
                        }
                    }
                }
            }

            else -> throw IllegalArgumentException("Unknown instance type")
        }
    }

    routing {
        webSocket("/ws") {
            // Client/player connected
            println("Client connected")

            // Send old changed data to client
            Synk.dataForNewClient(instance).forEach {
                send(it)
            }

            val player = GameEntity(UUID.randomUUID().toString())
            player.connection = this

            instance.players += player // is synced to clients

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val msg = frame.readText()
                    println("Received message from client: $msg")
                }
            } finally {
                // Client/player disconnected
                println("Client with uuid ${player.uuid} disconnected")
                player.connection = null
                instance.players -= player // is synced to clients
            }
        }
    }
}