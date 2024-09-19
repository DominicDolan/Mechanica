package com.mechanica.engine.samples.loop

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.delta.RenderableDouble
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setStartingScene { RenderVariableDemo() }
        setViewport(height = 12.0)
        setFullscreen(false)
        setDeltaTimeCalculator(DeltaCalculator.multiUpdateCalculator(1.0/90.0))
    }

    Game.loop()
}

class RenderVariableDemo : Scene() {
    var position: Double = 0.0
    private val renderablePosition = RenderableDouble.create({ position }, { 6.0 })
    override fun update(delta: Double) {

        position += 6.0*delta

        if (position > 10.0) {
            position = -10.0
        }
    }

    override fun render(draw: Drawer) {
        draw.red.circle(position, 2.0, 0.5)
        draw.red.circle(renderablePosition.value, -2.0, 0.5)
    }
}