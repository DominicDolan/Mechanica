package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.defaultFont
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.FontLoader
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.engine.text.LwjglStandardFont
import java.nio.ByteBuffer

class LwjglFontLoader : FontLoader {

    override fun font(ttfFile: ByteBuffer, initializer: FontAtlasConfiguration.() -> Unit): Font {
        return LwjglStandardFont(ttfFile, initializer)
    }

    override fun defaultFont(): Font {
        return Font.Companion.defaultFont
    }
}
