package com.mechanica.engine.samples.ui.duke.context

interface DukeViewport {
    val x: Double
    val y: Double
    val width: Double
    val height: Double

    companion object {
        fun create(x: Double,
                   y: Double,
                   width: Double,
                   height: Double): DukeViewport {
            return object : DukeViewport {
                override val x = x
                override val y = y
                override val width = width
                override val height = height
            }
        }
    }
}