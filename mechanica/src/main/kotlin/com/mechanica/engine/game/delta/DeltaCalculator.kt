package com.mechanica.engine.game.delta

interface DeltaCalculator {

    fun updateAndRender(lastFrame: Double, thisFrame: Double, updater: Updater) {
        updater.updateAndRender(lastFrame, thisFrame)
    }

    fun Updater.updateAndRender(lastFrame: Double, thisFrame: Double)
}