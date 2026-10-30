package com.mechanica.engine.shaders.text

import com.mechanica.engine.text.FontAtlasConfiguration

interface FontFactory {
    fun font(ttfFile: ByteArray, initializer: FontAtlasConfiguration.() -> Unit): Font

    fun defaultFont(): Font

}