package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.vbo.VBO

internal class RectangleDrawerImpl(
        private val data: DrawData) : RectangleDrawer {

    private val model: Model

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        model = Model(position)
    }

    override fun rectangle() {
        data.cornerSize.x = data.scaleX.toDouble()
        data.cornerSize.y = data.scaleY.toDouble()
        data.draw(model)
    }

    override fun rectangle(x: Number, y: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        rectangle()
    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        data.setScale(width.toFloat(), height.toFloat())
        rectangle()
    }

}