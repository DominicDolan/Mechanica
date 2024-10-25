package com.mechanica.engine.samples.ui.duke.dimension

open class Dimension(
        open val origin: Double,
        open val size: Double,
        protected val bottomIsLow: Boolean = true) {

    constructor(bottomIsLow: Boolean = true) : this(0.0, 1.0, bottomIsLow)

    open val end: Double
        get() = origin + size

    open val center: Double get() = origin + size/2.0

    open val bottom: Double
        get() = if (bottomIsLow) origin
        else end

    open val top: Double
        get() = if (bottomIsLow) end
        else origin
}