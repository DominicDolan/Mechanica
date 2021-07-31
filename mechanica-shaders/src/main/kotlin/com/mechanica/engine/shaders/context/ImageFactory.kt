package com.mechanica.engine.shaders.context

import com.mechanica.engine.shaders.models.Image
import java.nio.ByteBuffer

interface ImageFactory {

    fun loadImage(id: Int): Image
    fun loadImage(buffer: ByteBuffer): Image

}