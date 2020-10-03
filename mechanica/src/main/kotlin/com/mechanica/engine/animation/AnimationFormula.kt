package com.mechanica.engine.animation

import com.mechanica.engine.util.extensions.constrain
import com.mechanica.engine.util.extensions.constrainLooped

class AnimationFormula(
        startTime: Double, endTime: Double,
        private var formula: AnimationFormulas.(Double) -> Double): AnimationController {

    private val formulas = AnimationFormulas(this)

    override var startTime: Double = startTime
        private set
    override var endTime: Double = endTime
        private set
    override var length: Double
        get() = super.length
        set(value) = length(value)

    override var looped = false
    override var paused = false

    val value: Double
        get() = formula(formulas, relativeTime)

    override var time = startTime
        private set(value) {
            field = if (looped) {
                value.constrainLooped(startTime, endTime)
            } else {
                if (value >= endTime) {
                    paused = true
                }
                value.constrain(startTime, endTime)
            }
        }
    private val relativeTime: Double
        get() = time - startTime

    constructor(length: Double, formula: AnimationFormulas.(Double) -> Double) : this(0.0, length, formula)

    override fun goTo(time: Double) {
        this.time = time
    }

    fun start(start: Double) {
        this.startTime = start
    }

    fun length(length: Double) {
        endTime = startTime + length
    }

    fun end(end: Double) {
        this.endTime = end
    }

    override fun update(delta: Double) {
        if (!paused) {
            time += delta
        }
    }

}