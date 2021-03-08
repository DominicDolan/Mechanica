package com.mechanica.engine.scenes.scenes.sprites

import com.cave.library.vector.vec2.VariableVector2
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.DynamicView
import com.mechanica.engine.game.view.DynamicViewable
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene

abstract class DynamicSprite(
        x: Double = 0.0,
        y: Double = 0.0,
        width: Double = 1.0,
        height: Double = 1.0,
        order: Int = 0,
) : Scene(order), DynamicViewable {

    override val position: VariableVector2 = VariableVector2.create(x, y)
    override val view: DynamicView by lazy { DynamicViewableView(width, height) }

    constructor(
            view: View,
            order: Int = 0,
    ) : this(view.x, view.y, view.width, view.height, order)

    abstract override fun render(draw: Drawer)


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