package com.mechanica.engine.samples.ui.duke.context

interface DukeWindow {
    val x: Double
    val y: Double
    val width: Double
    val height: Double
    val bottom: Double
        get() = y
    val top: Double
        get() = y + height
    val left: Double
        get() = x
    val right: Double
        get() = x + width

    companion object {
        fun create(x: Double,
                   y: Double,
                   width: Double,
                   height: Double): DukeWindow {
            return object : DukeWindow {
                override val x = x
                override val y = y
                override val width = width
                override val height = height
            }
        }
    }
}