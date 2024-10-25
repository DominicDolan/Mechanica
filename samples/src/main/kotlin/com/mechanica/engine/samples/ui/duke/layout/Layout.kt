package com.mechanica.engine.samples.ui.duke.layout

import com.mechanica.engine.samples.ui.duke.context.DukeContext
import com.mechanica.engine.samples.ui.duke.dimension.Dimension

interface Layout {
    val x: Double
    val y: Double
    val width: Double
    val height: Double
    val center: Point
    val left: Double
    val bottom: Double
    val top: Double
    val right: Double

    companion object {
        fun layoutToString(layout: Layout) = "layout: x: ${layout.x}, y: ${layout.y}, width: ${layout.width}, height: ${layout.height}"

        fun create(context: DukeContext, x: Double, y: Double, width: Double, height: Double,): Layout {
            return object : StaticLayout(context) {
                override val x = x
                override val y = y
                override val width = width
                override val height = height
            }
        }
    }
}

abstract class StaticLayout(context: DukeContext) : Layout {
    private val horizontal = object : Dimension(context.leftIsLow) {
        override val origin: Double
            get() = x
        override val size: Double
            get() = width
    }

    private val vertical = object : Dimension(context.bottomIsLow) {
        override val origin: Double
            get() = y
        override val size: Double
            get() = height
    }

    override val center: Point = object : Point {
        override val x: Double
            get() = horizontal.center
        override val y: Double
            get() = vertical.center
    }

    override val left: Double
        get() = horizontal.bottom
    override val bottom: Double
        get() = vertical.bottom
    override val top: Double
        get() = vertical.top
    override val right: Double
        get() = horizontal.top

    override fun toString(): String {
        return Layout.layoutToString(this)
    }
}
