package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer
import kotlin.math.min

class MultiUpdateCalculator(updateTime: Double) : DeltaCalculator {
    private var dt = updateTime
    private var accumulator = 0.0
    private var variableTrackers = ArrayList<RenderableDouble>()

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        val frameLength = thisFrame - lastFrame
        accumulator += frameLength
        dt = min(frameLength, dt)

        while (accumulator > dt) {
            val preUpdateTime = Timer.now
            update(dt)

            for (i in variableTrackers.indices) {
                variableTrackers[i].update()
            }
            val updateRealTime = Timer.now - preUpdateTime

            if (updateRealTime > dt) {
                break
            }

            accumulator -= dt
        }

        for (i in variableTrackers.indices) {
            variableTrackers[i].preRenderUpdate(accumulator, dt)
        }

        render()
    }

    fun addVariableTracker(tracker: RenderableDouble) {
        variableTrackers.add(tracker)
    }
}