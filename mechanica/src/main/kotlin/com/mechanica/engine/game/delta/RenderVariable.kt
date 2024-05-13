package com.mechanica.engine.game.delta

import com.mechanica.engine.game.Game

class RenderableDouble(private val getValue: () -> Double, getVelocity: (() -> Double)? = null){
    var value: Double = 0.0
        private set

    private var lastValue: Double = getValue()
    private var currentValue: Double = getValue()

    private var renderableVelocity: RenderableDouble? = if (getVelocity != null) RenderableDouble(getVelocity) else null

    fun update() {
        lastValue = currentValue
        currentValue = getValue()
    }

    fun preRenderUpdate(accumulated: Double, delta: Double) {
        val velocity = renderableVelocity?.value ?: accumulated
        val fraction = velocity/delta
        value = currentValue  + fraction*(currentValue - lastValue)
    }

    companion object {
        fun create(getValue: () -> Double, getVelocity: (() -> Double)?): RenderableDouble {
            val double = RenderableDouble(getValue, getVelocity)
            val deltaCalculator = Game.deltaCalculator
            if (deltaCalculator !is MultiUpdateCalculator) {
                throw IllegalStateException("Tried to use RenderableDouble but the DeltaCalculator is not an instance of MultiUpdateCalculator." +
                        "Use 'setDeltaTimeCalculator(DeltaCalculator.multiUpdateCalculator())' during Game configuration to use the right DeltaCalculator")
            }

            deltaCalculator.addVariableTracker(double)

            return double
        }

        fun create(getValue: () -> Double): RenderableDouble {
            return create(getValue, null)
        }
    }
}