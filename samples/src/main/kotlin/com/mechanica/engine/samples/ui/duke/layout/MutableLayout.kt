package com.mechanica.engine.samples.ui.duke.layout

import com.mechanica.engine.samples.ui.duke.context.DukeContext
import com.mechanica.engine.samples.ui.duke.dimension.MutableDimension

interface MutableLayout : Layout {
    override var x: Double
    override var y: Double
    override var width: Double
    override var height: Double
    override val center: MutablePoint
    override var left: Double
    override var bottom: Double
    override var top: Double
    override var right: Double

    private class MutableLayoutImpl(context: DukeContext) : MutableLayout {
        private val horizontal = MutableDimension(context.leftIsLow)
        private val vertical = MutableDimension(context.bottomIsLow)

//        abstract val parent: Layout
//        abstract val sibling: Layout
//        abstract val isFirst: Boolean

        override var x: Double
            get() = horizontal.origin
            set(value) { horizontal.origin = value }
        override var width: Double
            get() = horizontal.size
            set(value) { horizontal.size = value }
        override var right: Double
            get() = horizontal.top
            set(value) { horizontal.top = value }
        override var left: Double
            get() = horizontal.bottom
            set(value) { horizontal.bottom = value }

        override var y: Double
            get() = vertical.origin
            set(value) { vertical.origin = value }
        override var height: Double
            get() = vertical.size
            set(value) { vertical.size = value }
        override var top: Double
            get() = vertical.top
            set(value) { vertical.top = value }
        override var bottom: Double
            get() = vertical.bottom
            set(value) { vertical.bottom = value }

        override val center: Center = Center()


        internal fun resetPriorities() {
            horizontal.resetPriorities()
            vertical.resetPriorities()
        }


        inner class Center: MutablePoint {
            override var x: Double
                get() = horizontal.center
                set(value) {
                    horizontal.center = value
                }
            override var y: Double
                get() = vertical.center
                set(value) {
                    vertical.center = value
                }

            override fun set(other: Point) {
                horizontal.center = other.x
                vertical.center = other.y
            }
        }

        override fun toString() = Layout.layoutToString(this)
    }

    companion object {
        fun create(context: DukeContext): MutableLayout = MutableLayoutImpl(context)
    }
}