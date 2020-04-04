package demo.color

import drawer.Drawer
import game.Game
import gl.renderer.RectangleRenderer
import input.Cursor
import input.Keyboard
import input.Mouse
import org.joml.Matrix4f
import state.State
import util.colors.Color
import util.colors.hex
import util.colors.hsl
import util.extensions.degrees
import util.extensions.vec

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setResolution(2000, 2000)
        setStartingState { StartMain() }
    }

    Game.run()
}


private class StartMain : State() {
    val renderer = RectangleRenderer()
    val transformation = Matrix4f()
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val mouseX = Mouse.viewX/Game.view.width + 0.5
        val mouseY = Mouse.viewY/ Game.view.height + 0.5
        draw.black.text("x: $mouseX, y: $mouseY", 0.1, Game.view.left, Game.view.top - 0.13)
        draw.black.rectangle(0, 0, 1, 1)
        draw.yellow.rectangle(-0.1, 0.5, 0.6, 0.6)
        renderer.color = hex(0x008000FF)
        renderer.radius = (Mouse.viewX/Game.view.width).toFloat() + 0.5f
//        transformation.rotate(15f, 0f, 0f, 1f)
        renderer.render(transformation = transformation)
    }

}