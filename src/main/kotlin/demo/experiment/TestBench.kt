@file:Suppress("unused") // Experimentation class, many functions/variables will be unused
package demo.experiment

import debug.DebugDrawer
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
            .setDebugMode(true)
            .setViewPort(height = 5.0)
            .setStartingState { TestState() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class TestState : State() {
//    val renderer = FontRenderer()
    override fun update(delta: Double) {
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
        if (Keyboard.D.hasBeenPressed) {
            Game.viewX += 1.0
        }
        if (Keyboard.A.hasBeenPressed) {
            Game.viewX -= 1.0
        }
        DebugDrawer.drawText("Radius: $radius")
        val pixelScale = ((Game.height.toDouble())/Game.viewHeight)
        val pixelRadius = radius*pixelScale
        DebugDrawer.drawText("Radius: $radius")
        DebugDrawer.drawText("Pixel radius: $pixelRadius")
        DebugDrawer.drawText("Game height: ${Game.viewHeight}, resolution: ${Game.height}x${Game.width}")
        draw.ui.red.rotated(rotation.degrees).circle(Cursor.viewX - 0.2, Cursor.viewY + 0.2, radius)
        draw.ui.green.circle(Cursor.viewX - 0.2, Cursor.viewY + 0.2, 0.1)
        draw.centered.blue.rectangle(0, 0, 1.0, 4.9)
    }



}


