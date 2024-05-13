package com.mechanica.engine.game.delta

import com.mechanica.engine.game.Game

class RenderableDouble(private val getValue: () -> Double){
    var value: Double = 0.0
        private set

    private var lastValue: Double = getValue()
    private var currentValue: Double = getValue()

    fun update(delta: Double) {
        lastValue = currentValue
        currentValue = getValue()
    }

    fun preRenderUpdate(accumulated: Double, delta: Double, lastFrame: Double, thisFrame: Double) {
        val fraction = accumulated/delta
        value = currentValue  + fraction*(currentValue - lastValue)
    }

    companion object {
        fun create(getValue: () -> Double): RenderableDouble {
            val double = RenderableDouble(getValue)
            val deltaCalculator = Game.deltaCalculator
            if (deltaCalculator !is MultiUpdateCalculator) {
                throw IllegalStateException("Tried to use RenderableDouble but the DeltaCalculator is not an instance of MultiUpdateCalculator." +
                        "Use 'setDeltaTimeCalculator(DeltaCalculator.multiUpdateCalculator())' during Game configuration to use the right DeltaCalculator")
            }

            deltaCalculator.addVariableTracker(double)

            return double
        }
    }
}