package com.mechanica.engine.shaders.text

import com.mechanica.engine.text.FontAtlasConfiguration
import java.nio.ByteBuffer

interface FontFactory {
    fun font(ttfFile: ByteBuffer, initializer: FontAtlasConfiguration.() -> Unit): Font

    fun defaultFont(): Font

}