package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray

internal class RectangleDrawerImpl(
        private val data: DrawData) : RectangleDrawer {

    private val model: Model

    init {
        val position = Attribute.location(0).vec3().createUnitQuad()

        val disableTexCoords = object : Bindable {
            override fun bind() {
                glDisableVertexAttribArray(1)
            }
        }
        model = Model(position, disableTexCoords)

    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        data.setScale(width.toFloat(), height.toFloat())
        drawRectangle()
    }

    private fun drawRectangle() {
        data.cornerSize.x = data.scaleX.toDouble()
        data.cornerSize.y = data.scaleY.toDouble()
        data.draw(model)
    }

}