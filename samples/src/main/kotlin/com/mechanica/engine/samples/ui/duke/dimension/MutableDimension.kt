package com.mechanica.engine.samples.ui.duke.dimension

class MutableDimension(startIsLow: Boolean = true) : Dimension(startIsLow) {
    private var changed = true

    private var _origin = 0.0
    override var origin: Double
        get() = calculatedOrigin
        set(value) {
            prepareToSet(ORIGIN,value != _origin)
            _origin = value
        }

    private var _end = 0.0
    override var end: Double
        get() = calculatedOrigin + calculatedSize
        set(value) {
            prepareToSet(END,value != _end)
            _end = value
        }

    private var _size = 0.0
    override var size: Double
        get() = calculatedSize
        set(value) {
            prepareToSet(SIZE,value != _size)
            _size = value
        }

    private var _center = 0.0
    override var center: Double
        get() = calculatedOrigin + calculatedSize/2.0
        set(value) {
            prepareToSet(CENTER,value != _center)
            _center = value
        }

    override var bottom: Double
        get() = super.bottom
        set(value) {
            if (bottomIsLow) origin = value
            else end = value
        }
    override var top: Double
        get() = super.top
        set(value) {
            if (bottomIsLow) end = value
            else origin = value
        }

    private var firstPriority: Int = ORIGIN
    private var secondPriority: Int = SIZE

    private fun prepareToSet(newPriority: Int, hasValueChanged: Boolean) {
        changed = hasValueChanged || (firstPriority != newPriority) || changed
        shiftPriorities(newPriority)
    }

    private fun shiftPriorities(newPriority: Int) {
        if (firstPriority != newPriority) {
            secondPriority = firstPriority
        }
        firstPriority = newPriority
    }

    private val output = Output(0.0, 1.0)

    private val calculatedOrigin: Double
        get() {
            if (changed) { set() }
            return output.origin
        }

    private val calculatedSize: Double
        get() {
            if (changed) { set() }
            return output.size
        }

    private fun set() {
        val fp = firstPriority
        val sp = secondPriority
        fun isFirstAndSecondPriority(i1: Int, i2: Int) = (fp == i1 && sp == i2) || (fp == i2 && sp == i1)

        when {
            isFirstAndSecondPriority(ORIGIN, SIZE) -> {
                output.origin = _origin
                output.size = _size
            }
            isFirstAndSecondPriority(ORIGIN, END) -> {
                output.origin = _origin
                output.size = _end - _origin
            }
            isFirstAndSecondPriority(ORIGIN, CENTER) -> {
                output.origin = _origin
                output.size = (_center - _origin)*2.0
            }
            isFirstAndSecondPriority(SIZE, END) -> {
                output.size = _size
                output.origin = _end - _size
            }
            isFirstAndSecondPriority(SIZE, CENTER) -> {
                output.size = _size
                output.origin = _center - _size/2.0
            }
            isFirstAndSecondPriority(END, CENTER) -> {
                output.size = (_end - _center)*2
                output.origin = _end - output.size
            }
        }
        changed = false
    }

    fun resetPriorities() {
        firstPriority = ORIGIN
        secondPriority = SIZE
    }

    private inner class Output(var origin: Double, var size: Double)

    companion object {
        private const val ORIGIN = 0
        private const val END = 1
        private const val SIZE = 2
        private const val CENTER = 3
    }
}