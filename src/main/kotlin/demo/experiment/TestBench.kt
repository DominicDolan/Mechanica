@file:Suppress("unused") // Experimentation class, many functions/variables will be unused
package demo.experiment

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.renderer.FontRenderer
import gl.script.ShaderDeclarations
import input.Cursor
import input.Keyboard
import input.Mouse
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import state.State
import util.extensions.degrees
import util.extensions.vec
import kotlin.math.sqrt

fun main() {
    createGameInstance()
}

fun createGameInstance() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setViewPort(height = 5.0)
            .setStartingState { TestState() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class TestState : State() {
    override fun update(delta: Double) {
    }

    var radius = 1.0
    var rotation = 0.0
    val vec4 = Vector4f()
    val vec3 = Vector3f()
    override fun render(draw: Drawer) {
        if (Mouse.SCROLL_DOWN.hasBeenPressed) {
            radius /= 1.0 + 0.1*Mouse.SCROLL_DOWN.distance
        }
        if (Mouse.SCROLL_UP.hasBeenPressed) {
            radius *= 1.0 + 0.1*Mouse.SCROLL_DOWN.distance
        }
        if (Keyboard.LEFT.hasBeenPressed) {
            rotation -= 5.0
        }
        if (Keyboard.RIGHT.hasBeenPressed) {
            rotation += 5.0
        }
        if (Keyboard.D.hasBeenPressed) {
            Game.viewX += 1.0
        }
        if (Keyboard.A.hasBeenPressed) {
            Game.viewX -= 1.0
        }

        draw.ui.red.rotated(rotation.degrees).circle(Cursor.viewX - 0.2, Cursor.viewY + 0.2, radius)
    }



}


