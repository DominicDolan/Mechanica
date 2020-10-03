package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.engine.text.LwjglStandardFont

class LwjglFontLoader : FontLoader {
    override fun font(res: Resource, initializer: FontAtlasConfiguration.() -> Unit) = LwjglStandardFont(res, initializer)
}
