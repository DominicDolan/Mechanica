package com.mechanica.engine.samples.scenes

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.unit.angle.degrees

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { SceneDemo() }
    }

    Game.loop()
}

class SceneDemo : Scene(), Inputs by Inputs.create() {

    var angle = 0.0

    override fun update(delta: Double) {
        // Increase the angle by 30 degrees every second
        angle += delta*30.0
    }

    override fun render(draw: Drawer) {
        draw.centered.red.rotated(angle.degrees).rectangle()
    }
}