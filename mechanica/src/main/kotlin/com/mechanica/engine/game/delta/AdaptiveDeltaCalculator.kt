package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer
import com.mechanica.engine.util.ValueStatistics
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

internal class AdaptiveDeltaCalculator(frameTime: Double) : DeltaCalculator {
    private var targetUpdateCount = 1.0
    private var dt = frameTime/targetUpdateCount

    private val frameTimes = ValueStatistics(frameTime, 100.0)
    private val updateTimes = ValueStatistics(dt, 100.0)
    private val simulatedVsRealDifference = ValueStatistics(frameTime, 100.0)

    private var frameCounter = 0

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        var frameDelta = (thisFrame - lastFrame)
        frameTimes.add(frameDelta)
        frameCounter++

        for (i in 1..targetUpdateCount.roundToInt()) {
            val actualUpdateTime = update(this)
            updateTimes.add(actualUpdateTime)

            frameDelta -= dt

            if (actualUpdateTime > dt) { break }

        }

        simulatedVsRealDifference.add(frameDelta)

        if (frameCounter > 100) {
            checkUpdateCount()
            checkDt()
            frameCounter = 0
        }

        render()
    }

    private fun update(updater: Updater): Double {
        val preUpdate = Timer.now
        updater.update(dt)
        return Timer.now - preUpdate
    }

    private fun checkDt() {
        val discrepancy = simulatedVsRealDifference.average

        if (abs(discrepancy) > frameTimes.average*0.3) {
            dt = frameTimes.average/targetUpdateCount
        }
    }

    private fun checkUpdateCount() {
        val averageActual = updateTimes.average
        if (averageActual > 0.7 * dt) {
            targetUpdateCount = max(1.0, targetUpdateCount / 2.0)
            dt = frameTimes.average / targetUpdateCount
        } else if (averageActual < 0.1*dt
                && targetUpdateCount < 8.0
                && averageActual*targetUpdateCount < 0.1*frameTimes.average
        ) {
            targetUpdateCount *= 2.0
            dt = frameTimes.average / targetUpdateCount
        }
    }
}