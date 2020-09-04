package com.mechanica.engine.drawer.superclass.circle

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.qualifiers.Attribute
import org.lwjgl.opengl.GL20
import kotlin.math.min

class CircleDrawerImpl(
        private val data: DrawData): CircleDrawer {

    private val model: Model

    init {
        val position = Attribute.location(0).vec3().createUnitQuad()

        val disableTexCoords = object : Bindable {
            override fun bind() {
                GL20.glDisableVertexAttribArray(1)
            }
        }
        model = Model(position, disableTexCoords)
    }

    private fun drawCircle() {
        val radius = if (data.radius > 0.0) data.radius
        else {
            data.radius = 0.5f
            0.5f
        }

        val diameter = radius*2.0

        data.setScale(diameter.toFloat(), diameter.toFloat())
        data.cornerSize.set(diameter, diameter)

        val origin = data.modelOrigin
        if (!origin.wasSet) {
            origin.set(0.5, 0.5)
        }

        data.draw(model)
    }

    override fun circle(x: Number, y: Number, radius: Number) {
        if (radius.toFloat() > 0.0) {
            data.radius = radius.toFloat()
        }
        data.setTranslate(x.toFloat(), y.toFloat())
        drawCircle()
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