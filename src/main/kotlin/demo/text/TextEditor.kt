package demo.text

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.utils.loadImage
import input.Cursor
import input.Key
import input.Keyboard
import input.Mouse
import gl.models.Model
import gl.renderer.FontRenderer
import org.joml.Matrix4f
import resources.Res
import state.State
import util.colors.hex
import util.extensions.restrain
import util.extensions.vec
import kotlin.math.max
import kotlin.math.min

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
//            .setFullscreen(true, true)
            .setViewPort(height = 10.0)
            .setStartingState { StartText() }

    Game.start(options)
    Game.update()
    Game.destroy()
}


private class StartText : State() {
    val colors = loadImage(Res.image["testImage"])
    val renderer = FontRenderer()

    val model = Model()
    val transformation = Matrix4f()
    val specialKeys = HashMap<Key, Char>()

    var cursor = 0

    init {
        renderer.text = ""

        renderer.position = vec(-Game.viewWidth.toFloat()/2f, Game.viewHeight.toFloat()/2f - renderer.fontSize)

        specialKeys[Keyboard.SPACE] = ' '
        specialKeys[Keyboard.PERIOD] = '.'
        specialKeys[Keyboard.COMMA] = ','
        specialKeys[Keyboard.ENTER] = '\n'
    }

    override fun update(delta: Double) {
        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            Game.viewHeight *= 1.0 + Mouse.SCROLL_DOWN.distance/10.0
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            Game.viewHeight /= 1.0 + Mouse.SCROLL_UP.distance/10.0
        }

        if (Keyboard.ANY.hasBeenPressed) {
            for (key in Keyboard.pressed) {
                val char = getChar(key)

                if (char != null) {
                    addLetter(cursor, char)
                }
            }
        }

        if (Keyboard.BACKSPACE.hasBeenPressed) {
            removeLetter(cursor)
            if (cursor > 0) {
                cursor--
            }
        }
        if (Keyboard.DELETE.hasBeenPressed) {
            removeLetter(cursor+1)
        }

        if (Keyboard.LEFT.hasBeenPressed) {
            cursor = max(0, cursor-1)
        }
        if (Keyboard.RIGHT.hasBeenPressed) {
            cursor = min(renderer.text.length-1, cursor+1)
        }
        if (Keyboard.UP.hasBeenPressed) {
            val pos = renderer.from(cursor).getPosition()
            cursor = renderer.from(vec(pos.x, pos.y + 1.0)).getIndex()
        }
        if (Keyboard.DOWN.hasBeenPressed) {
            val pos = renderer.from(cursor).getPosition()
            cursor = renderer.from(vec(pos.x, pos.y - 1.0)).getIndex()
        }

        if (Mouse.MB1.hasBeenPressed) {
            cursor = renderer.from(Cursor.viewX, Cursor.viewY).getIndex()
        }

    }

    override fun render(draw: Drawer) {
        draw.centered.color(hex(0xC0C0C0FF)).rectangle(0, 0, Game.viewWidth, Game.viewHeight)
        val pos = renderer.from(cursor).getPosition()
        draw.blue.rectangle(pos.x, pos.y - 0.1*renderer.fontSize, 0.05, 0.75*renderer.fontSize)
        renderer.render(model, transformation)
    }

    fun addLetter(index: Int, char: Char) {
        val fullText = renderer.text
        val safeIndex = index.restrain(0, fullText.length)

        val before = if (index <= 0) ""
            else fullText.substring(0 until safeIndex)

        val after = if (index >= fullText.length-1) ""
                    else fullText.substring(safeIndex until fullText.length)

        renderer.text = before + char + after
        cursor++

    }

    fun removeLetter(index: Int) {
        val fullText = renderer.text
        val safeIndex = index.restrain(0, fullText.length)

        val before = if (index <= 0) ""
        else fullText.substring(0 until max(0,safeIndex-1))

        val after = if (index >= fullText.length) ""
        else fullText.substring(safeIndex until fullText.length)

        renderer.text = before + after
    }

    private fun getChar(key: Key): Char? {
        val str = key.toString()
        if (str.length == 1) {
            return if (!Keyboard.SHIFT()) str.toLowerCase()[0]
                    else str[0]
        }
        return specialKeys[key]
    }

}

