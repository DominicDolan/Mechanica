package com.mechanica.engine.shaders.context

import com.mechanica.engine.shaders.models.Image

data class ImageDimensions(val width: Int, val height: Int)

interface ImageFactory {

    fun dimensions(encoded: ByteArray): ImageDimensions
    /** Bind an image to a texture unit and restore unit zero as the active unit. */
    fun bindToUnit(image: Image, unit: Int)
    fun loadImage(id: Int): Image
    fun loadImage(encoded: ByteArray): Image

}