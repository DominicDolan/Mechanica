package com.mechanica.engine.samples.color

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Mouse
import com.mechanica.engine.scenes.scenes.MainScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setResolution(2000, 2000)
        setStartingState { StartMain() }
    }

    Game.run()
}


private class StartMain : MainScene() {
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