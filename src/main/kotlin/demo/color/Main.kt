package demo.color

import drawer.Drawer
import game.Game
import input.Cursor
import input.Keyboard
import input.Mouse
import state.State
import util.colors.Color
import util.colors.hex
import util.colors.hsl
import util.extensions.degrees
import util.extensions.vec

fun main() {
    Game.configure {
        setViewport(height = 3.0)
        setResolution(2000, 2000)
        setStartingState { StartMain() }
    }

    Game.run()
}


private class StartMain : State() {
    val renderer = RectangleRenderer()
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val mouseX = Mouse.viewX/Game.view.width + 0.5
        val mouseY = Mouse.viewY/ Game.view.height + 0.5
        renderer.mouse = vec(mouseX, mouseY)
        draw.text("x: $mouseX, y: $mouseY", 0.1, Game.view.left, Game.view.top - 0.13)
        draw.black.rectangle(0, 0, 1, 1)
        renderer.color = hex(0x008000FF)
        renderer.render()
    }

}