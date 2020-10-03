package com.mechanica.engine.game.delta

class BasicVariableCalculator : DeltaCalculator {
    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        val delta = thisFrame - lastFrame
        update(delta)
        render()
    }
}