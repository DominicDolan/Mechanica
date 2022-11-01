package com.mechanica.engine.samples.input

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { MouseExample() }
    }

    Game.loop()
}

class MouseExample : Scene(), Inputs by Inputs.create() {
    private var circlePositions = ArrayList<Vector2>()

    override fun update(delta: Double) {
        if (mouse.MB1.hasBeenPressed) {
            circlePositions.add(vec(mouse.world))
        }

    }

    override fun render(draw: Drawer) {

        for (position in circlePositions) {
            draw.red.circle(position, 0.5)
        }

        draw.red.alpha(0.5).circle(mouse.world, 0.5)

        if (mouse.scroll.hasBeenPressed) {
            Game.world.y += mouse.scroll.distance
        }
    }
}