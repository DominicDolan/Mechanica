package com.mechanica.engine.game.delta

import com.mechanica.engine.util.Timer
import com.mechanica.engine.util.ValueStatistics
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class AdaptiveDeltaCalculator(frameTime: Double, updateTime: Double) : DeltaCalculator {

    private val frameTimes = ValueStatistics(frameTime, 100.0)

    private var dt = updateTime
    private val updateTimes = ValueStatistics(dt, 100.0)

    private var accumulator = 0.0

    private val frameRate = FrameRateCounter()

    override fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double) {
        var frameDelta = (thisFrame - lastFrame)

        frameRate.update()
        frameRate.screenLog()
        frameTimes.add(frameDelta)

        val updatesRequiredPerFrame = max(1.0, frameTimes.average/dt)

        for (i in 1..updatesRequiredPerFrame.roundToInt()) {
            val updateTime = update()

            if (!checkUpdateTime(updateTime)) { break }

            frameDelta -= dt
        }

        accumulator += frameDelta

        render()

    }

    private fun Updater.update(): Double {
        val preUpdate = Timer.now
        update(dt)
        val updateTime = Timer.now - preUpdate

        updateTimes.add(updateTime)
        return updateTime
    }

    private fun checkUpdateTime(updateTime: Double): Boolean {
        if (updateTime > dt) {
            if (updateTimes.average > 0.7*dt) {
                dt = min(2 * dt, frameTimes.average)
            }
            return false
        }
        return true
    }

}