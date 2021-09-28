package com.mechanica.engine.samples.color

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setResolution(2000, 2000)
        setStartingScene { StartMain() }
    }

    Game.loop()
}


private class StartMain : Scene() {
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val mouseX = Mouse.ui.x/Game.world.width + 0.5
        val mouseY = Mouse.ui.y/ Game.world.height + 0.5
        draw.black.text("x: $mouseX, y: $mouseY", 0.1, Game.world.left, Game.world.top - 0.13)
        draw.black.rectangle(0, 0, 1, 1)
        draw.yellow.rectangle(-0.1, 0.5, 0.6, 0.6)
    }

}