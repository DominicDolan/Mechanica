package com.mechanica.engine.drawer.superclass.rectangle

import com.cave.library.vector.arrays.Vector2Arrays
import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.attributes.FloatAttributeBinder
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType

internal class RectangleDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer) : RectangleDrawer {

    private val model: Model

    init {
        val position = AttributeArray.createPositionArray(Vector2Arrays.createUnitSquare())

        val attributeBinder = FloatAttributeBinder.create(GlslLocation.Companion.create(1), ShaderType.vec2())
        val disableTexCoords = object : Bindable {
            override fun bind() {
                attributeBinder.unbind()
            }
        }
        model = Model(position, disableTexCoords)

    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        state.setTranslate(x.toDouble(), y.toDouble())
        state.setScale(width.toDouble(), height.toDouble())
        drawRectangle()
    }

    private fun drawRectangle() {
        val scale = state.transformation.scale
        state.shader.cornerSize.set(scale.x, scale.y)
        state.setModel(model)
        renderer.render(state, 0f, 0f, false)
    }

}