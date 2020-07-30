package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.models.Image
import com.mechanica.engine.unit.vector.LightweightVector

interface ImageDrawer {
    fun image(image: Image, x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun image(image: Image, xy: LightweightVector, wh: LightweightVector) = image(image, xy.x, xy.y, wh.x, wh.y)

}