package demo.text

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.utils.loadImage
import input.Keyboard
import input.Keys
import input.Mouse
import models.Model
import org.joml.Matrix4f
import resources.Res
import state.State
import util.colors.hex

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
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

    init {
        renderer.text = "N&D=<3"

    }

    override fun update(delta: Double) {
        if (Keyboard.S.hasBeenPressed) {
            Game.viewHeight *= 1.1
        }
        if (Keyboard.W.hasBeenPressed) {
            Game.viewHeight /= 1.1
        }

        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            println("Scroll down: ${Mouse.SCROLL_DOWN.distance}")
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            println("Scroll up: ${Mouse.SCROLL_UP.distance}")
        }
    }

    override fun render(draw: Drawer) {
        draw.centered.color(hex(0xC0C0C0FF)).rectangle(0, 0, Game.viewWidth, Game.viewHeight)
        draw.centered.red.rectangle(0, 0, 1, 1)
        transformation.translate(-10f, 0f, 0f)
        renderer.render(model, transformation)
        transformation.identity()
    }

}

