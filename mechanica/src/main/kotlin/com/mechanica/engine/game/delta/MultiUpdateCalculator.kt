package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer
import kotlin.math.min

class MultiUpdateCalculator(updateTime: Double) : DeltaCalculator {
    private val dt = updateTime
    private var accumulator = 0.0
    private var variableTrackers = ArrayList<RenderableDouble>()

    override val timeStep: Double
        get() = dt

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        val frameLength = thisFrame - lastFrame
        accumulator += frameLength

        while (accumulator > dt) {
            val preUpdateTime = Timer.now
            update(dt)

            for (i in variableTrackers.indices) {
                variableTrackers[i].update()
            }

            // Credit the step before deciding whether to bail out. Subtracting only on the way
            // round the loop leaks the work done by the final step, so an update that costs more
            // than dt leaves a backlog that grows every frame and is never drained.
            accumulator -= dt

            if (Timer.now - preUpdateTime > dt) {
                // Updates cost more than they simulate, so the accumulator can only run away.
                // Drop the backlog and let the game run slow rather than spiral.
                accumulator = min(accumulator, dt)
                break
            }
        }

        for (i in variableTrackers.indices) {
            variableTrackers[i].preRenderUpdate(accumulator, dt)
        }

        render()
    }

    override fun step(updater: Updater, delta: Double) {
        updater.update(delta)

        for (i in variableTrackers.indices) {
            variableTrackers[i].update()
        }

        // A single step lands exactly on a simulated frame, so there is nothing to extrapolate.
        accumulator = 0.0
        for (i in variableTrackers.indices) {
            variableTrackers[i].preRenderUpdate(0.0, dt)
        }

        updater.render()
    }

    override fun resync() {
        accumulator = 0.0
    }

    fun addVariableTracker(tracker: RenderableDouble) {
        variableTrackers.add(tracker)
    }
}