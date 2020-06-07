package com.mechanica.engine.drawer.subclasses.transformation

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class TransformationDrawerImpl(drawer: Drawer, private val data: DrawData): TransformationDrawer, Drawer by drawer {
    override fun translate(x: Number, y: Number): TransformationDrawer {
        data.setTranslate(x.toFloat(), y.toFloat())
        return this
    }

    override fun scale(x: Number, y: Number): TransformationDrawer {
        data.setScale(x.toFloat(), y.toFloat())
        return this
    }

    override fun invoke(x: Number, y: Number, scaleX: Number, scaleY: Number): TransformationDrawer {
        translate(x, y)
        scale(scaleX, scaleY)
        return this
    }
}