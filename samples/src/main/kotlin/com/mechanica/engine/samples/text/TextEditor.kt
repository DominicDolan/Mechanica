package com.mechanica.engine.samples.text

import com.mechanica.engine.color.hex
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.input.Keyboard
import com.mechanica.engine.input.Mouse
import com.mechanica.engine.state.State
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.constrain
import org.joml.Matrix4f
import kotlin.math.max
import kotlin.math.min

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { StartText() }
    }

    Game.run()
}


private class StartText : State() {
    val renderer = FontRenderer()

    val model = Model()
    val transformation = Matrix4f()

    val startPosition = vec(-Game.view.width.toFloat()/2f, Game.view.height.toFloat()/2f - renderer.fontSize)

    var cursor = 0

    init {
        renderer.text = ""

        renderer.position.set(startPosition)

    }

    override fun update(delta: Double) {
        fun setViewPosition() {
            Game.view.x = startPosition.x + Game.view.width/2.0
            Game.view.y = startPosition.y + 1.0 - Game.view.height/2.0
        }
        if (Mouse.scrollDown.hasBeenPressed) {
            Game.view.height *= 1.0 + Mouse.scrollDown.distance/10.0
            setViewPosition()
        }
        if (Mouse.scrollUp.hasBeenPressed) {
            Game.view.height /= 1.0 + Mouse.scrollUp.distance/10.0
            setViewPosition()
        }

        if (Keyboard.textInput.hasBeenInput.isNotEmpty()) {
            addLetter(cursor, Keyboard.textInput.inputText)
        }

        if (Keyboard.backspace.hasBeenPressed) {
            removeLetter(cursor)
            if (cursor > 0) {
                cursor--
            }
        }
        if (Keyboard.delete.hasBeenPressed) {
            removeLetter(cursor+1)
        }
        if (Keyboard.enter.hasBeenPressed) {
            addLetter(cursor, "\n")
        }

        if (Keyboard.left.hasBeenPressed) {
            cursor = max(0, cursor-1)
        }
        if (Keyboard.right.hasBeenPressed) {
            cursor = min(renderer.text.length, cursor+1)
        }
        if (Keyboard.up.hasBeenPressed) {
            val pos = renderer.from(cursor).getPosition()
            cursor = renderer.from(vec(pos.x - 0.15, pos.y + 1.0)).getIndex()
        }
        if (Keyboard.down.hasBeenPressed) {
            val pos = renderer.from(cursor).getPosition()
            cursor = renderer.from(vec(pos.x - 0.15, pos.y - 1.0)).getIndex()
        }

        if (Mouse.MB1.hasBeenPressed) {
            cursor = renderer.from(Mouse.world.x, Mouse.world.y).getIndex()
        }

    }

    override fun render(draw: Drawer) {
        draw.color(hex(0xC0C0C0FF)).background()

        val pos = renderer.from(cursor).getPosition()
        draw.blue.rectangle(pos.x, pos.y - 0.1*renderer.fontSize, 0.05, 0.75*renderer.fontSize)
        renderer.render(transformation)
    }

    fun addLetter(index: Int, str: String) {
        val fullText = renderer.text
        val safeIndex = index.constrain(0, fullText.length)

        val before = if (index <= 0) ""
            else fullText.substring(0 until safeIndex)

        val after = if (index >= fullText.length-1) ""
                    else fullText.substring(safeIndex until fullText.length)

        renderer.text = before + str + after
        cursor++

    }

    fun removeLetter(index: Int) {
        val fullText = renderer.text
        val safeIndex = index.constrain(0, fullText.length)

        val before = if (index <= 0) ""
        else fullText.substring(0 until max(0,safeIndex-1))

        val after = if (index >= fullText.length) ""
        else fullText.substring(safeIndex until fullText.length)

        renderer.text = before + after
    }

}

