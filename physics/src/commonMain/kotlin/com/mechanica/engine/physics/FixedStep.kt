package com.mechanica.engine.physics

import com.mechanica.engine.time.FixedStepAccumulator

/** Standalone physics clock. Do not nest this inside a fixed-step game calculator. */
class FixedStep(
    private val world: PhysicsWorld,
    val stepSeconds: Double = 1.0 / 60.0,
    val substeps: Int = 4,
    val maxStepsPerUpdate: Int = 8,
) {
    private val accumulator = FixedStepAccumulator(stepSeconds, maxStepsPerUpdate)

    init {
        require(stepSeconds.toFloat().isFinite() && stepSeconds.toFloat() > 0f) { "Invalid Box2D time step" }
        require(substeps > 0) { "Substeps must be positive" }
    }

    val interpolationAlpha: Double get() = accumulator.interpolationAlpha

    /** Advances the world and returns the number of physics steps performed. */
    fun update(frameSeconds: Double): Int {
        world.checkOpen()
        return accumulator.advance(frameSeconds) { world.step(it, substeps) }
    }
}
