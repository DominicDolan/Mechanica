package com.mechanica.engine.time

import kotlin.math.min

/** A bounded fixed-timestep accumulator shared by game and physics loops. */
class FixedStepAccumulator(val stepSeconds: Double, val maxStepsPerUpdate: Int) {
    private val maxAccumulated: Double
    private var accumulated = 0.0
    private var showCurrent = false

    init {
        require(stepSeconds.isFinite() && stepSeconds > 0.0) { "Step duration must be finite and positive" }
        require(maxStepsPerUpdate > 0) { "Maximum steps must be positive" }
        maxAccumulated = stepSeconds * maxStepsPerUpdate
        require(maxAccumulated.isFinite()) { "Maximum accumulated time must be finite" }
    }

    /** Render blend from the previous to the current simulation state. */
    val interpolationAlpha: Double get() = if (showCurrent) 1.0 else (accumulated / stepSeconds).coerceIn(0.0, 1.0)

    /** Excess elapsed time is discarded to bound catch-up work. */
    fun advance(elapsedSeconds: Double, update: (Double) -> Unit): Int {
        require(elapsedSeconds.isFinite() && elapsedSeconds >= 0.0) { "Elapsed time must be finite and non-negative" }
        accumulated = min(maxAccumulated, accumulated + min(elapsedSeconds, maxAccumulated))
        var steps = 0
        while (accumulated >= stepSeconds && steps < maxStepsPerUpdate) {
            update(stepSeconds)
            accumulated -= stepSeconds
            steps++
            showCurrent = false
        }
        return steps
    }

    /** Discard leftover time and display the latest simulation state until the next step. */
    fun reset() {
        accumulated = 0.0
        showCurrent = true
    }
}
