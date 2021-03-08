package com.mechanica.engine.game.view

import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.Drawer

interface Viewable {
    val view: View

    val position: Vector2
        get() = view.xy

    val Drawer.inView: Drawer
        get() = drawInView(this, view)

    fun drawInView(draw: Drawer, view: View): Drawer = draw.centered.transformed.translate(view.x, view.y).scale(view.wh)

    companion object {
        fun create(view: View) = object : Viewable {
            override val view: View = view
        }
    }
}

interface MovingViewable : Viewable{
    override val position: VariableVector2

    companion object {
        fun create(
                x: Double = 0.0,
                y: Double = 0.0,
                width: Double = 1.0,
                height: Double = 1.0,
        ): MovingViewable = MovingViewableImpl(x, y, width, height)

        fun create(view: View): MovingViewable = MovingViewableImpl(view.x, view.y, view.width, view.height)
    }
}

private open class MovingViewableImpl(
        x: Double = 0.0,
        y: Double = 0.0,
        width: Double = 1.0,
        height: Double = 1.0,
) : MovingViewable {

    override val position = VariableVector2.create(x + width/2.0, y + height/2.0)

    override val view: View by lazy { MovingView(width, height) }

    inner class MovingView(
            override val width: Double,
            override val height: Double) : View {

        override val x: Double
            get() = position.x
        override val y: Double
            get() = position.y

        override val xy: VariableVector2
            get() = position

        override var wh: Vector2 = object : Vector2 {
            override val x: Double
                get() = this@MovingView.width
            override val y: Double
                get() = this@MovingView.height
        }
    }
}

interface DynamicViewable : MovingViewable {
    override val view: DynamicView

    companion object {
        fun create(x: Double = 0.0,
                   y: Double = 0.0,
                   width: Double = 1.0,
                   height: Double = 1.0
        ): DynamicViewable = DynamicViewableImpl(x, y, width, height)

        fun create(view: View): DynamicViewable = DynamicViewableImpl(view.x, view.y, view.width, view.height)
    }
}

private class DynamicViewableImpl(
        x: Double = 0.0,
        y: Double = 0.0,
        width: Double = 1.0,
        height: Double = 1.0) : MovingViewableImpl(x, y, width, height), DynamicViewable {

    override val view: DynamicView by lazy { DynamicViewableView(width, height) }

    inner class DynamicViewableView(
            override var width: Double,
            override var height: Double) : DynamicView {

        override var x: Double
            get() = position.x
            set(value) { position.x = value}
        override var y: Double
            get() = position.y
            set(value) { position.y = value }

        override val xy: VariableVector2 = object : VariableVector2 {
            override var x: Double
                get() = this@DynamicViewableView.x
                set(value) {this@DynamicViewableView.x = value }
            override var y: Double
                get() = this@DynamicViewableView.y
                set(value) { this@DynamicViewableView.y = value}
        }

        override val wh: VariableVector2 = object : VariableVector2 {
            override var x: Double
                get() = this@DynamicViewableView.width
                set(value) {this@DynamicViewableView.width = value }
            override var y: Double
                get() = this@DynamicViewableView.height
                set(value) { this@DynamicViewableView.height = value}
        }
    }
}