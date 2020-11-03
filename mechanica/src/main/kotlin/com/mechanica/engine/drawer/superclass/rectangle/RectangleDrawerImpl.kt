package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray

internal class RectangleDrawerImpl(
        private val state: DrawState) : RectangleDrawer {

    private val model: Model

    init {
        val position = AttributeArray.createFrom(Attribute.position).createUnitQuad()

        val disableTexCoords = object : Bindable {
            override fun bind() {
                glDisableVertexAttribArray(1)
            }
        }
        model = Model(position, disableTexCoords)

    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        state.setTranslate(x.toFloat(), y.toFloat())
        state.setScale(width.toFloat(), height.toFloat())
        drawRectangle()
    }

    private fun drawRectangle() {
        state.cornerSize.x = state.transformation.scale.x.toDouble()
        state.cornerSize.y = state.transformation.scale.y.toDouble()
        state.draw(model)
    }

}