package com.mechanica.engine.drawer.superclass.circle

import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray
import org.lwjgl.opengl.GL20
import kotlin.math.min

class CircleDrawerImpl(
        private val state: DrawState): CircleDrawer {

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
        val radius = if (state.radius > 0.0) state.radius
        else {
            state.radius = 0.5f
            0.5f
        }

        val diameter = radius*2.0

        state.setScale(diameter.toFloat(), diameter.toFloat())
        state.cornerSize.set(diameter, diameter)

        val origin = state.origin.normalized
        if (!origin.wasChanged) {
            origin.set(0.5, 0.5)
        }

        state.draw(model)
    }

    override fun circle(x: Number, y: Number, radius: Number) {
        if (radius.toFloat() > 0.0) {
            state.radius = radius.toFloat()
        }
        state.setTranslate(x.toFloat(), y.toFloat())
        drawCircle()
    }

    override fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        state.setTranslate(x.toFloat(), y.toFloat())

        val minorAxis = min(width.toDouble(), height.toDouble())
        state.setScale(width.toFloat(), height.toFloat())
        state.radius = (minorAxis/2f).toFloat()
        state.cornerSize.set(minorAxis, minorAxis)

        state.draw(model)
    }
}