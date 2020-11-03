package com.mechanica.engine.drawer.subclasses.transformation

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.unit.angle.Radian
import org.joml.Matrix4f

class TransformationDrawerImpl(drawer: Drawer, private val state: DrawState): TransformationDrawer, Drawer by drawer {
    override fun translate(x: Number, y: Number): TransformationDrawer {
        state.setTranslate(x.toFloat(), y.toFloat())
        return this
    }

    override fun scale(x: Number, y: Number): TransformationDrawer {
        state.setScale(x.toFloat(), y.toFloat())
        return this
    }

    override fun skew(horizontal: Radian, vertical: Radian): TransformationDrawer {
        state.setSkew(horizontal.toDouble().toFloat(), vertical.toDouble().toFloat())
        return this
    }

    override fun matrix(matrix: Matrix4f): TransformationDrawer {
        state.transformation.userMatrix = matrix
        return this
    }

    override fun invoke(x: Number, y: Number, scaleX: Number, scaleY: Number): TransformationDrawer {
        translate(x, y)
        scale(scaleX, scaleY)
        return this
    }
}