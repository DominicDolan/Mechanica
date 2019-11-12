@file:Suppress("unused") // Experimentation class, many functions/variables will be unused
package demo.experiment

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import state.State

fun main() {
    println("For return")
    `return Character Is Always Null Warning`(RETURN.toChar())
    println("for space")
    `return Character Is Always Null Warning`(SPACE_ASCII.toChar())

}

private const val SPACE_ASCII = 32
private const val RETURN = '\n'.toInt()

@Suppress("FunctionName")
fun `return Character Is Always Null Warning`(c: Char) {
    val returnCharacter = c.toInt() == RETURN
    if (c.toInt() == SPACE_ASCII || returnCharacter) {

        var added = false
        if (!returnCharacter) {
            added = true
            println("added = savedLines[currentLine].attemptToAddWord()")
        }

        if (!added || returnCharacter) {
            println("returnCharacter: $returnCharacter")
            println("currentLine++")
            println("avedLines[currentLine].set(metaData.getSpaceWidth(), text.fontSize.toDouble(), text.maxLineSize.toDouble())")
            println("savedLines[currentLine].attemptToAddWord()")
        }
        return
    }
}

fun createGameInstance() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setViewPort(height = 10.0)
            .setStartingState { TestState() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class TestState : State() {
    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {

    }

}


