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
import util.extensions.vec
import util.units.MutableVector

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
    init {
        renderer.text = "Ve//{ygj@^')ggggggggggggggggggggggggggggggggg\nHelloggggggggggggggggggggggggggggggggggggg\nMotogggggggg4ggggggggggggggggggggggggg\nPlaygggggggggggggggggggggggggggggggggggggggggggggggggggggggggg, play"
        println(renderer.text.length)
        println(renderer.text.indexOf('4'))
    }

    var position = MutableVector(0.0,0.0)
    override fun update(delta: Double) {
        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            Game.viewHeight *= 1.0 + Mouse.SCROLL_DOWN.distance/10.0
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            Game.viewHeight /= 1.0 + Mouse.SCROLL_UP.distance/10.0
        }
        if (Keyboard.W()) {
            position.y += 1.0*delta
        }
        if (Keyboard.S()) {
            position.y -= 1.0*delta
        }
        if (Keyboard.A()) {
            position.x -= 1.0*delta
        }
        if (Keyboard.D()) {
            position.x += 1.0*delta
        }

    }

    override fun render(draw: Drawer) {
        draw.centered.color(hex(0xC0C0C0FF)).rectangle(0, 0, Game.viewWidth, Game.viewHeight)
        draw.red.rectangle(position.x, position.y, 5, 1)
        draw.blue.rectangle(position.x, position.y - 1f, 5, 1)
        draw.red.rectangle(position.x, position.y - 2f, 5, 1)
        draw.blue.rectangle(position.x, position.y - 3f, 5, 1)
        renderer.render(model, transformation)
    }

}

