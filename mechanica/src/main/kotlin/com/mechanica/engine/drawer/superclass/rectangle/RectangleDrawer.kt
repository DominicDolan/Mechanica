package com.mechanica.engine.drawer.superclass.rectangle

import com.mechanica.engine.unit.vector.LightweightVector

interface RectangleDrawer {
    fun rectangle()
    fun rectangle(x: Number, y: Number)
    fun rectangle(x: Number, y: Number, width: Number, height: Number)
    fun rectangle(xy: LightweightVector, wh: LightweightVector) = rectangle(xy.x, xy.y, wh.x, wh.y)
}