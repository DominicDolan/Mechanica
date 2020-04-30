package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.gl.utils.Image
import com.mechanica.engine.unit.vector.LightweightVector

interface ImageDrawer {
    fun image(image: Image)

    fun image(image: Image, x: Number, y: Number)
    fun image(image: Image, x: Number, y: Number, width: Number, height: Number)
    fun image(image: Image, xy: LightweightVector, wh: LightweightVector) = image(image, xy.x, xy.y, wh.x, wh.y)

}