package com.mechanica.engine.drawer.superclass.rectangle

import com.cave.library.vector.vec2.InlineVector

interface RectangleDrawer {
    fun rectangle(x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun rectangle(xy: InlineVector, wh: InlineVector) = rectangle(xy.x, xy.y, wh.x, wh.y)
}