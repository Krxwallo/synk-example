import io.github.krxwallo.synk.synkvar.*

class GameInstance {
    // Use a list and not a mutable list to properly trigger recomposition when adding or removing elements (with += and -=)
    var players by SynkVar(listOf<GameEntity>())

    var intProperty by SynkVar(1)
}