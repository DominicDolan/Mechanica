package drawer.superclass.image

import graphics.Image
import util.units.LightweightVector

interface ImageDrawer {
    fun image(image: Image)

    fun image(image: Image, x: Number, y: Number)
    fun image(image: Image, x: Number, y: Number, width: Number, height: Number)
    fun image(image: Image, xy: LightweightVector, wh: LightweightVector) = image(image, xy.x, xy.y, wh.x, wh.y)

}