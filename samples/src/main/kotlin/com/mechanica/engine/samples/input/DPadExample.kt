package com.mechanica.engine.samples.input

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.DPad
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.plusAssign
import com.mechanica.engine.unit.vector.times
import com.mechanica.engine.unit.vector.vec

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { DPadExample() }
    }

    Game.loop()
}

class DPadExample : Scene(), Inputs by Inputs.create() {
    private var position = DynamicVector.create(0.0, 0.0)
    private val speed = 2.0
    private val dPad = DPad.createWithWASD(speed)

    override fun update(delta: Double) {
        position += vec(dPad.value)*delta
    }

    override fun render(draw: Drawer) {
        // Draw a red circle at the specified position
        draw.centered.red.circle(position)
    }
}