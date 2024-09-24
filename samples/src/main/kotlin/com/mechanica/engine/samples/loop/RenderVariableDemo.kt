package com.mechanica.engine.samples.loop

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.cave.library.angle.times
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.delta.RenderableDouble
import com.mechanica.engine.scenes.scenes.Scene
import kotlin.math.cos
import kotlin.math.sin

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
    var angle = 0.0.degrees
    private val renderablePosition = RenderableDouble.create({ position }, { -10.0*sin(angle.toRadians()) })
    override fun update(delta: Double) {

        angle += 360.degrees * delta
        position = 0.5*cos(angle.toRadians())

    }

    override fun render(draw: Drawer) {
        draw.red.circle(position, 2.0, 0.5)
        draw.red.circle(renderablePosition.value , -2.0, 0.5)
    }
}