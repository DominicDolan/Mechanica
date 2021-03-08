package com.mechanica.engine.drawer.superclass.image

import com.cave.library.vector.vec2.InlineVector
import com.mechanica.engine.shaders.models.Image

interface ImageDrawer {
    fun image(image: Image, x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun image(image: Image, xy: InlineVector, wh: InlineVector) = image(image, xy.x, xy.y, wh.x, wh.y)

    fun colorizedImage(image: Image, x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun colorizedImage(image: Image, xy: InlineVector, wh: InlineVector) = colorizedImage(image, xy.x, xy.y, wh.x, wh.y)

}