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
        renderableVelocity?.update()
    }

    fun preRenderUpdate(accumulated: Double, delta: Double) {
        val rv = renderableVelocity
        if (rv == null) {
            val fraction = accumulated/delta
            value = currentValue  + fraction*(currentValue - lastValue)
        } else {
            rv.preRenderUpdate(accumulated, delta)
            val velocity = rv.value
            value = currentValue + velocity * accumulated
        }
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