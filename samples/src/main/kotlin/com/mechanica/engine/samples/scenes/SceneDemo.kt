package com.mechanica.engine.samples.scenes

import com.cave.library.angle.degrees
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.setNewMainScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { SceneDemo1() }
    }

    Game.loop()
}

class SceneDemo1 : Scene(), Inputs by Inputs.create() {

    var angle = 0.0

    override fun update(delta: Double) {
        angle += delta*30.0
        if (keyboard.space.hasBeenPressed) {
            setNewMainScene { SceneDemo2() }
        }
    }

    override fun render(draw: Drawer) {
        draw.ui.centered.red.rotated(angle.degrees).rectangle()
        draw.centered.darkGrey.text("Press space to change scene", 0.5, 0.0, Game.ui.top - 1.0)
    }
}

class SceneDemo2 : Scene(), Inputs by Inputs.create() {

    var xPosition = 0.0

    override fun update(delta: Double) {
        xPosition += delta*3.0
        xPosition %= (Game.ui.width/2.0)
        if (keyboard.space.hasBeenPressed) {
            setNewMainScene { SceneDemo1() }
        }
    }

    override fun render(draw: Drawer) {
        draw.radius(0.5).green.rectangle(x = xPosition, width = 2.0)
        draw.centered.darkGrey.text("Press space again to go back", 0.5, 0.0, Game.ui.bottom + 1.0)
    }
}