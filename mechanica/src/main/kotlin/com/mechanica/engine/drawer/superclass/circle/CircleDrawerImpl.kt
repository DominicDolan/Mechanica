package com.mechanica.engine.drawer.superclass.circle

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.vbo.VBO
import kotlin.math.min

class CircleDrawerImpl(
        private val data: DrawData): CircleDrawer {

    private val model: Model

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        model = Model(position)
    }

    override fun circle() {
        val radius = if (data.radius > 0.0) data.radius else 1f
        val diameter = radius*2.0

        data.setScale(diameter.toFloat(), diameter.toFloat())
        data.cornerSize.set(diameter, diameter)

        val origin = data.modelOrigin
        if (!origin.wasSet) {
            origin.set(0.5, 0.5)
        }

        data.draw(model)
    }

    override fun circle(x: Number, y: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        circle()
    }

    override fun circle(x: Number, y: Number, radius: Number) {
        data.radius = radius.toFloat()
        circle(x, y)
    }

    override fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())

        val minorAxis = min(width.toDouble(), height.toDouble())
        data.setScale(width.toFloat(), height.toFloat())
        data.radius = (minorAxis/2f).toFloat()
        data.cornerSize.set(minorAxis, minorAxis)

        data.draw(model)
    }
}