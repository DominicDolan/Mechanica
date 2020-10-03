package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer

class MultiUpdateCalculator(updateTime: Double) : DeltaCalculator {
    private val dt = updateTime
    private var accumulator = 0.0

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {

        while (accumulator > dt) {
            val preUpdateTime = Timer.now
            update(dt)
            val updateRealTime = Timer.now - preUpdateTime

            if (updateRealTime > dt) {
                break
            }

            accumulator -= dt
        }

        render()
    }
}