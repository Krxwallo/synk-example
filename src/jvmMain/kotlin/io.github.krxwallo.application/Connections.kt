package io.github.krxwallo.application

import GameEntity
import io.ktor.server.websocket.*

val connections = HashMap<String, DefaultWebSocketServerSession>()

var GameEntity.connection
    get() = connections[uuid]
    set(value) {
        value?.let {
            connections[uuid] = it
        } ?: connections.remove(uuid)
    }