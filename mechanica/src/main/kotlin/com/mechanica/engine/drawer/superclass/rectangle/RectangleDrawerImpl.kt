package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray
import org.lwjgl.opengl.GL20.glDisableVertexAttribArray

internal class RectangleDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer) : RectangleDrawer {

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
        val scale = state.transformation.scale.variable
        state.shader.cornerSize.set(scale.x.toDouble(), scale.y.toDouble())
        state.setModel(model)
        renderer.render(state, 0f, 0f, false)
    }

}