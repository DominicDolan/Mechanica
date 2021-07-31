package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.defaultFont
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.FontFactory
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.engine.text.LwjglStandardFont
import java.nio.ByteBuffer

class LwjglFontFactory : FontFactory {

    override fun font(ttfFile: ByteBuffer, initializer: FontAtlasConfiguration.() -> Unit): Font {
        return LwjglStandardFont(ttfFile, initializer)
    }

    override fun defaultFont(): Font {
        return Font.Companion.defaultFont
    }
}
