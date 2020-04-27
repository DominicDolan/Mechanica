@file:Suppress("unused") // Experimentation class, many functions/variables will be unused
package demo.experiment

import drawer.Drawer
import game.Game
import input.Keyboard
import input.Mouse
import org.joml.Vector3f
import org.joml.Vector4f
import state.State

fun main() {
    createGameInstance()
}

fun createGameInstance() {
    Game.configure {
        setViewport(height = 5.0)
        setStartingState { TestState() }
    }

    Game.run()
}

private class TestState : State() {
//    val renderer = FontRenderer()
    override fun update(delta: Double) {
        if (Keyboard.D()) {
            Game.view.x += 1.0*delta
        }
        if (Keyboard.A()) {
            Game.view.x -= 1.0*delta
        }
        if (Keyboard.W()) {
            Game.view.y += 1.0*delta
        }
        if (Keyboard.S()) {
            Game.view.y -= 1.0*delta
        }
    }

    var radius = 1.0
    var rotation = 0.0
    val vec4 = Vector4f()
    val vec3 = Vector3f()
    override fun render(draw: Drawer) {
        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            radius /= 1.0 + 0.05*Mouse.SCROLL_DOWN.distance
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            radius *= 1.0 + 0.05*Mouse.SCROLL_DOWN.distance
        }
        if (Keyboard.LEFT.hasBeenPressed) {
            rotation -= 5.0
        }
        if (Keyboard.RIGHT.hasBeenPressed) {
            rotation += 5.0
        }

        val pixelScale = ((Game.window.height.toDouble())/Game.view.height)

        draw.world.centered.blue.rectangle(0, 0, 1.0, 4.9)
        draw.world.red.circle(Mouse.world.x, Mouse.world.y, radius)
        draw.ui.green.circle(Mouse.view.x, Mouse.view.y, 0.1)
    }



}


