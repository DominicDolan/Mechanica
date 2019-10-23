package demo

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import state.State
import util.extensions.degrees
import util.extensions.r
import util.extensions.theta
import util.extensions.vec

fun main() {
    val angle = 30.degrees
    val radius = 50.0
    println("Start, angle: $angle, radius: $radius")
    val vec = vec(radius, angle)
    println("theta: ${vec.theta.toDegrees().toDouble()}, r: ${vec.r}")

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


