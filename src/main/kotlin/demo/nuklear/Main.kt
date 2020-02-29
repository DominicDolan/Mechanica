package demo.nuklear

import display.Game
import display.GameOptions
import drawer.Drawer
import org.lwjgl.nuklear.NkContext
import state.State


fun main() {
    val options = GameOptions()
            .setResolution(1920, 1080)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    init {
        val ctx = NkContext.create()
    }

    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {

    }
}