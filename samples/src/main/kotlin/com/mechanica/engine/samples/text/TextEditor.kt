package com.mechanica.engine.samples.text

import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.TextInput
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.extensions.constrain
import kotlin.math.max
import kotlin.math.min

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { StartText() }
        configureDebugMode {
            constructionDraws = true
        }
    }

    Game.loop()
}


private class StartText : Scene() {
    val renderer = FontRenderer()

    val transformation = Matrix4.identity()

    val startPosition = vec(-Game.world.width.toFloat()/2f, Game.world.height.toFloat()/2f - renderer.fontSize)

    var cursor = 0

    private val sb = StringBuilder()

    init {
        renderer.text = ""

        renderer.position.set(startPosition)

    }

    override fun update(delta: Double) {
        fun setViewPosition() {
            Game.world.x = startPosition.x + Game.world.width/2.0
            Game.world.y = startPosition.y + 1.0 - Game.world.height/2.0
        }

        if (Mouse.scroll.hasBeenPressed) {
            Game.world.height /= 1.0 + Mouse.scroll.distance/10.0
            setViewPosition()
        }

        if (TextInput.hasTextInput) {
            addLetterFromKeyboard()
        }

        if (Keyboard.backspace.hasBeenPressed) {
            removeLetter(cursor)
            if (cursor > 0) {
                cursor--
            }
        }

        if (Keyboard.delete.hasBeenPressed) {
            removeLetter(cursor + 1)
        }
        if (Keyboard.enter.hasBeenPressed) {
            addLetter('\n', cursor)
            cursor++
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
            cursor = renderer.from(Mouse.world.x, Mouse.world.y).getIndex().constrain(0, renderer.text.length)
        }

    }

    override fun render(draw: Drawer) {
        draw.black.background()

        val pos = renderer.from(cursor).getPosition()
        draw.blue.rectangle(pos.x, pos.y - 0.1*renderer.fontSize, 0.05, 0.75*renderer.fontSize)
        renderer.render(transformation)
    }

    fun updateText() {
        renderer.text = sb.toString()
    }

    fun addLetterFromKeyboard() {
        val changed = TextInput.getCodepoints(sb, cursor)
        cursor += changed
        updateText()
    }

    fun addLetter(char: Char, index: Int) {
        sb.insert(index, char)
        updateText()
    }

    fun removeLetter(index: Int) {
        sb.delete(max(0, index-1), index)
        updateText()
    }
}

