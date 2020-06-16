package com.mechanica.engine.animation

class AnimationFormulas(private val formula: AnimationFormula) {
    val startTime: Double
        get() = formula.startTime
    val endTime: Double
        get() = formula.endTime
    val length: Double
        get() = endTime - startTime

    fun progress(time: Double): Double {
        return time / length
    }

    fun absoluteTime(time: Double): Double = time + startTime

    fun linear(time: Double, start: Double, end: Double, timeSpan: Double = length): Double {
        val slope = (end - start)/timeSpan
        return lineEquation(time, slope, start)
    }

    fun lineEquation(time: Double, slope: Double, yIntercept: Double): Double {
        return slope*time + yIntercept
    }

    fun sin(time: Double, frequency: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0): Double {
        return amplitude* kotlin.math.sin(frequency * time + phase)
    }

    fun quadratic(time: Double, a: Double = 1.0, b: Double = 1.0, c: Double = 1.0): Double {
        return time*time*a + time*b + c
    }

    fun quadraticDescending(time: Double, start: Double, end: Double, timeSpan: Double = length): Double {
        return quadratic(time, -(start-end)/(timeSpan*timeSpan), 0.0, start)
    }

    fun quadraticAscending(time: Double, start: Double, end: Double, timeSpan: Double = length): Double {
        val diff = (start-end)
        val a = diff/(timeSpan*timeSpan)
        val b = -2*diff/timeSpan
        val c = start
        return quadratic(time, a, b, c)
    }

    fun quadraticBump(time: Double, base: Double, bumpSize: Double, timeSpan: Double = length): Double {
        val constant = 4*bumpSize

        val a = -constant/(timeSpan*timeSpan)
        val b = constant/timeSpan
        val c = base

        return quadratic(time, a, b, c)
    }

    companion object {
        fun linear(start: Double, end: Double): AnimationFormulas.(Double) -> Double = { linear(it, start, end) }

        fun sin(frequency: Double = 1.0, amplitude: Double = 1.0, phase: Double = 0.0): AnimationFormulas.(Double) -> Double {
            return { sin(it, frequency, amplitude, phase) }
        }

        fun quadratic(a: Double = 1.0, b: Double = 1.0, c: Double = 1.0): AnimationFormulas.(Double) -> Double {
            return { quadratic(it, a, b, c) }
        }

        fun quadraticDescending(start: Double, end: Double): AnimationFormulas.(Double) -> Double {
            return { quadraticDescending(it, start, end) }
        }

        fun quadraticAscending(start: Double, end: Double): AnimationFormulas.(Double) -> Double {
            return { quadraticAscending(it, start, end) }
        }

        fun quadraticBump(base: Double, bumpSize: Double): AnimationFormulas.(Double) -> Double {
            return { quadraticBump(it, base, bumpSize) }
        }
    }
}