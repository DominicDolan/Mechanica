package com.mechanica.engine.samples.color

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.WorldScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setResolution(2000, 2000)
        setStartingScene { StartMain() }
    }

    Game.run()
}


private class StartMain : WorldScene() {
    override fun update(delta: Double) {

    }

    override fun render(draw: Drawer) {
        val mouseX = Mouse.ui.x/Game.view.width + 0.5
        val mouseY = Mouse.ui.y/ Game.view.height + 0.5
        draw.black.text("x: $mouseX, y: $mouseY", 0.1, Game.view.left, Game.view.top - 0.13)
        draw.black.rectangle(0, 0, 1, 1)
        draw.yellow.rectangle(-0.1, 0.5, 0.6, 0.6)
    }

}