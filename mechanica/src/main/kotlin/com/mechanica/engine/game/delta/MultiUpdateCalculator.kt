package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer

class MultiUpdateCalculator(updateTime: Double) : DeltaCalculator {
    private val dt = updateTime
    private var accumulator = 0.0
    private var variableTrackers = ArrayList<RenderableDouble>()

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        accumulator += thisFrame - lastFrame

        while (accumulator > dt) {
            val preUpdateTime = Timer.now
            update(dt)

            for (i in variableTrackers.indices) {
                variableTrackers[i].update(dt)
            }
            val updateRealTime = Timer.now - preUpdateTime

            if (updateRealTime > dt) {
                break
            }

            accumulator -= dt
        }

        for (i in variableTrackers.indices) {
            variableTrackers[i].preRenderUpdate(accumulator, dt, lastFrame, thisFrame)
        }

        render()
    }

    fun addVariableTracker(tracker: RenderableDouble) {
        variableTrackers.add(tracker)
    }
}