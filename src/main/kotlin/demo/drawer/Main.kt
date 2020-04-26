package demo.drawer

import debug.ScreenLog
import drawer.Drawer
import game.Game
import gl.utils.loadImage
import resources.Res
import state.State
import util.extensions.degrees

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { StartMain() }
        configureDebugMode { screenLog = true }
    }

    Game.run()
}

private class StartMain : State() {
    val image = loadImage(Res.image["testImage"])
    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        ScreenLog { "Logging to screen" }
        draw.red.centered.rectangle(0, 0, 1.0, 1.0)
        draw.green.rectangle(0.0, 0.0, 1.0, 1.0)
        draw.blue.rotated(3.degrees).about(1.0, 1.0).rectangle(-4.0, 0.0, 2.0, 1.0)
        draw.magenta.text("Hello, world", 1.0, 0, -2.0)
        draw.image(image, 2, 2, 2, 1)
        draw.cyan.circle(0.0, 2.0, 0.5)
        draw.darkGrey.centered.rectangle(0, 0, 0.5, 10.0)
        draw.grey.centered.rectangle(0, 0, 0.5, 9.5)
        draw.text("Hello, World!", 1.0, 0, 0)
    }

}


