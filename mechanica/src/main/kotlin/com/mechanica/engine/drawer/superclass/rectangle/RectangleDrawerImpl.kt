package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.models.Model
import com.mechanica.engine.utils.createUnitSquareVecArray
import org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN
import org.lwjgl.opengl.GL11.glDrawArrays

internal class RectangleDrawerImpl(
        private val data: DrawData) : RectangleDrawer {

    private val model: Model

    init {
        val position = Attribute(0).vec2().createBuffer(createUnitSquareVecArray())
        model = Model(position) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        }
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