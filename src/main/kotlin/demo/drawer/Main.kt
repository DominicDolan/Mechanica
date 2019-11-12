package demo.drawer

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import loader.loadImageFromResource
import loader.loadTexture
import resources.Res
import state.State
import util.extensions.degrees
import java.util.Enumeration

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    val image = loadImageFromResource(Res.image["colors"])
    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        draw.red.centered.rectangle(0, 0, 1.0, 1.0)
        draw.green.rectangle(0.0, 0.0, 1.0, 1.0)
        draw.blue.rotated(30.degrees).about(1.0, 1.0).rectangle(0.0, 0.0, 1.0, 1.0)
        draw.yellow.positional.rectangle(2.0, 1.0, 4.0, 0.0)
        draw.magenta.text("Hello, world", 1.0, -8.0, -4.0)
        draw.image(image, 0, 0, 2, 1)
        draw.cyan.circle(0.0, 0.0, 0.5)
        draw.text("Hello, World!", 1.0, 0, 0)
    }

}

