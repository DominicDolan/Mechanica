package com.mechanica.engine.samples.display

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.scenes.scenes.WorldScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { DisplayTestState() }
    }

    Game.run()
}

class DisplayTestState : WorldScene() {
    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {
        draw.yellow.background()
        draw.green.rectangle(-1.0, 0, 1, 1)
        draw.blue.circle(0, 3, 0.5)
        draw.magenta.text("Hello, mechanica", 1.0, 1.0, 0)

        draw.centered.red.rectangle(-3, 0, 1, 9.9)
    }
}