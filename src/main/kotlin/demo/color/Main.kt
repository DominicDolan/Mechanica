package demo.color

import drawer.Drawer
import game.Game
import input.Mouse
import org.joml.Matrix4f
import state.State

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setResolution(2000, 2000)
        setStartingState { StartMain() }
    }

    Game.run()
}


private class StartMain : State() {
    val transformation = Matrix4f()
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val mouseX = Mouse.view.x/Game.view.width + 0.5
        val mouseY = Mouse.view.y/ Game.view.height + 0.5
        draw.black.text("x: $mouseX, y: $mouseY", 0.1, Game.view.left, Game.view.top - 0.13)
        draw.black.rectangle(0, 0, 1, 1)
        draw.yellow.rectangle(-0.1, 0.5, 0.6, 0.6)
    }

}