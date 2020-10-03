package com.mechanica.engine.util

import kotlin.math.sqrt

class ValueStatistics(
        private val startingValue: Double,
        val sampleSize: Double) {

    var average: Double = startingValue
        private set

    var variance: Double = startingValue*0.5
        private set

    val standardDeviation: Double
        get() = sqrt(variance)

    var lastValue = startingValue
        private set

    var lastVariance = variance
        private set

    val lastStandardDeviation: Double
        get() = sqrt(variance)

    val isSteady: Boolean
        get() = variance < 0.25*average*average

    fun add(value: Double): ValueStatistics {
        lastValue = value
        addToAverage(value)
        lastVariance = varianceOf(value)
        addToVariance(lastVariance)
        return this
    }

    fun lastValueIsWithinDeviation(multiple: Double) = isWithinDeviation(lastValue, multiple)

    fun isWithinDeviation(value: Double, multiple: Double): Boolean {
        val deviation = multiple*standardDeviation
        return value > (average - deviation) && value < (average + deviation)
    }

    private fun addToAverage(value: Double) {
        average += (value - average) / sampleSize
    }

    private fun addToVariance(value: Double) {
        variance += (value - variance)/sampleSize
    }

    private fun varianceOf(value: Double): Double {
        val diff = (value - average)
        return diff*diff
    }

}