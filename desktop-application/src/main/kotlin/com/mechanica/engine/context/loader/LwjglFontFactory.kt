package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.defaultFont
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.FontFactory
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.engine.text.LwjglStandardFont
import com.mechanica.engine.utils.toDirectBuffer

class LwjglFontFactory : FontFactory {

    override fun font(ttfFile: ByteArray, initializer: FontAtlasConfiguration.() -> Unit): Font {
        return LwjglStandardFont(ttfFile.toDirectBuffer(), initializer)
    }

    override fun defaultFont(): Font {
        return Font.Companion.defaultFont
    }
}
