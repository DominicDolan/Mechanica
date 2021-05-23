package com.mechanica.engine.drawer.subclasses.transformation

import com.cave.library.angle.Radian
import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

class TransformationDrawerImpl(drawer: Drawer, private val state: DrawState): TransformationDrawer, Drawer by drawer {
    override fun translate(x: Number, y: Number): TransformationDrawer {
        state.setTranslate(x.toDouble(), y.toDouble())
        return this
    }

    override fun scale(x: Number, y: Number): TransformationDrawer {
        state.setScale(x.toDouble(), y.toDouble())
        return this
    }

    override fun skew(horizontal: Radian, vertical: Radian): TransformationDrawer {
        state.setSkew(horizontal.toDouble().toFloat(), vertical.toDouble().toFloat())
        return this
    }

    override fun matrix(matrix: Matrix4): TransformationDrawer {
        state.transformation.userMatrix = matrix
        return this
    }

    override fun invoke(x: Number, y: Number, scaleX: Number, scaleY: Number): TransformationDrawer {
        translate(x, y)
        scale(scaleX, scaleY)
        return this
    }
}