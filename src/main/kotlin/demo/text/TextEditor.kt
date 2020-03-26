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
import util.units.Vector
import java.lang.StringBuilder
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
//            .setDebugMode(true)
//            .setFullscreen(true, true)
            .setViewPort(height = 10.0)
            .setStartingState { StartText() }

    Game.start(options)
    Game.update()
    Game.destroy()
}


private class StartText : State() {
    val colors = ColorsTutorial()
    val renderer = FontRenderer()

    val heart = loadImage(Res.image["red-heart_2764"])
    val rofl = loadImage(Res.image["rolling-on-the-floor-laughing_1f923"])
    val smileHearts = loadImage(Res.image["smiling-face-with-hearts_1f970"])

    val model = Model()
    val transformation = Matrix4f()

    val startPosition = vec(-Game.viewWidth.toFloat()/2f, Game.viewHeight.toFloat()/2f - renderer.fontSize)

    var cursor = 0

    init {
        renderer.text = ""

        renderer.position = startPosition

    }

    override fun update(delta: Double) {
        fun setViewPosition() {
            Game.viewX = startPosition.x + Game.viewWidth/2.0
            Game.viewY = startPosition.y + 1.0 - Game.viewHeight/2.0
        }
        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            Game.viewHeight *= 1.0 + Mouse.SCROLL_DOWN.distance/10.0
            setViewPosition()
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            Game.viewHeight /= 1.0 + Mouse.SCROLL_UP.distance/10.0
            setViewPosition()
        }

        if (Keyboard.textInput.hasBeenInput.isNotEmpty()) {
            addLetter(cursor, Keyboard.textInput.inputText)
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
        if (Keyboard.ENTER.hasBeenPressed) {
            addLetter(cursor, "\n")
        }

        if (Keyboard.LEFT.hasBeenPressed) {
            cursor = max(0, cursor-1)
        }
        if (Keyboard.RIGHT.hasBeenPressed) {
            cursor = min(renderer.text.length, cursor+1)
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

    var showHeart = false
    var showRofl = false
    var showSmiles = false
    var roflPos: Vector = vec(0, 0)
    var smilesPos: Vector = vec(0, 0)
    override fun render(draw: Drawer) {
//        draw.color(hex(0xC0C0C0FF)).background()
        colors.render()
        if (Keyboard.CTRL() && Keyboard.N1.hasBeenPressed) {
            showHeart = true
        }
        if (Keyboard.CTRL() && Keyboard.N2.hasBeenPressed) {
            showRofl = true
            roflPos = renderer.from(renderer.text.length).getPosition()
        }
        if (Keyboard.CTRL() && Keyboard.N3.hasBeenPressed) {
            showSmiles = true
            smilesPos = renderer.from(renderer.text.length).getPosition()
        }
        if (showHeart) {
            draw.centered.image(heart, 0, 0, 3, 3)
        }
        if (showRofl){
            draw.normal.image(rofl, roflPos.x, roflPos.y - 0.2, 0.8, 0.8)
        }
        if (showSmiles) {
            draw.normal.image(smileHearts, smilesPos.x, smilesPos.y - 0.2, 0.8, 0.8)
        }
        val pos = renderer.from(cursor).getPosition()
        draw.blue.rectangle(pos.x, pos.y - 0.1*renderer.fontSize, 0.05, 0.75*renderer.fontSize)
        renderer.render(model, transformation)
    }

    fun addLetter(index: Int, str: String) {
        val fullText = renderer.text
        val safeIndex = index.restrain(0, fullText.length)

        val before = if (index <= 0) ""
            else fullText.substring(0 until safeIndex)

        val after = if (index >= fullText.length-1) ""
                    else fullText.substring(safeIndex until fullText.length)

        renderer.text = before + str + after
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

}

