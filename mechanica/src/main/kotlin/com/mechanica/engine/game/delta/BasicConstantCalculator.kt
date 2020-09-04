package com.mechanica.engine.game.delta

class BasicConstantCalculator(frameRate: Double) : DeltaCalculator {
    private val delta = 1.0/frameRate

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        update(delta)
        render()
    }
}