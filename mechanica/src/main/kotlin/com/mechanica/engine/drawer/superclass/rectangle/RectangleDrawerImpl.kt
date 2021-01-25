package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.attributes.FloatAttributeBinder
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType
import com.mechanica.engine.unit.vector.VectorShapes

internal class RectangleDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer) : RectangleDrawer {

    private val model: Model

    init {
        val position = AttributeArray.createPositionArray(VectorShapes.createUnitSquare())

        val attributeBinder = FloatAttributeBinder.create(GlslLocation.Companion.create(1), ShaderType.vec2())
        val disableTexCoords = object : Bindable {
            override fun bind() {
                attributeBinder.unbind()
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