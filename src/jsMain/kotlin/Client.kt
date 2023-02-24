import io.github.krxwallo.synk.Synk
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    // In this basic example we only use one instance, but there can be multiple, e.g. for multiple games running at
    // the same time.
    val instance = GameInstance()

    val client = HttpClient {
        install(WebSockets)
    }

    scope.launch {
        client.webSocket("ws://localhost:8081/ws") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val data = frame.readText()
                if (!Synk.onClientReceive(instance, data)) {
                    println("Received unknown data: $data")
                }
            }
        }
    }

    renderComposable("root") {
        P {
            Text("My cool synced int: ${instance.intProperty}")
        }

        H2 {
            Text("Players:")
        }
        Ul {
            instance.players.forEach {
                Li {
                    Text(it.uuid)
                }
            }
        }
    }
}