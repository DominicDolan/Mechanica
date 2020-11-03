package com.mechanica.engine.drawer.superclass.circle

import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray
import org.lwjgl.opengl.GL20
import kotlin.math.min

class CircleDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer): CircleDrawer {

    private val model: Model

    init {
        val position = AttributeArray.createFrom(Attribute.position).createUnitQuad()

        val disableTexCoords = object : Bindable {
            override fun bind() {
                GL20.glDisableVertexAttribArray(1)
            }
        }
        model = Model(position, disableTexCoords)
    }

    private fun drawCircle() {
        val radiusState = state.shader.radius
        val radius = if (radiusState.wasChanged) radiusState.value
        else {
            radiusState.value = 0.5
            0.5
        }

        val diameter = radius*2.0

        state.setScale(diameter.toFloat(), diameter.toFloat())
        state.shader.cornerSize.set(diameter, diameter)

        val origin = state.origin.normalized
        if (!origin.wasChanged) {
            origin.set(0.5, 0.5)
        }

        state.shader.model.variable = model

        renderer.render(state, 0f, 0f, false)
    }

    override fun circle(x: Number, y: Number, radius: Number) {
        if (radius.toFloat() > 0.0) {
            state.shader.radius.value = radius.toDouble()
        }
        state.setTranslate(x.toFloat(), y.toFloat())
        drawCircle()
    }

    override fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        state.setTranslate(x.toFloat(), y.toFloat())

        val minorAxis = min(width.toDouble(), height.toDouble())
        state.setScale(width.toFloat(), height.toFloat())
        state.shader.radius.value = (minorAxis/2.0)
        state.shader.cornerSize.set(minorAxis, minorAxis)

        state.shader.model.variable = model
        renderer.render(state, 0f, 0f, false)
    }
}