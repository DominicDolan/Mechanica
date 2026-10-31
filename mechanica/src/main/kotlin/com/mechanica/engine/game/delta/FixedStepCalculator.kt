package com.mechanica.engine.game.delta

import com.mechanica.engine.time.FixedStepAccumulator

/** Drives game updates at a fixed rate and renders between the last two updates. */
class FixedStepCalculator(
    override val timeStep: Double = 1.0 / 120.0,
    maxUpdatesPerFrame: Int = 8,
) : DeltaCalculator {
    private val accumulator = FixedStepAccumulator(timeStep, maxUpdatesPerFrame)

    override val interpolationAlpha: Double get() = accumulator.interpolationAlpha

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        accumulator.advance(thisFrame - lastFrame) { update(it) }
        render()
    }

    override fun step(updater: Updater, delta: Double) {
        updater.update(delta)
        accumulator.reset()
        updater.render()
    }

    override fun resync() = accumulator.reset()
}
