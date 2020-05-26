package com.mechanica.engine.context.loader

import com.mechanica.engine.text.LwjglStandardFont
import com.mechanica.engine.text.Font
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.text.FontAtlasConfiguration

class LwjglFontLoader : FontLoader {
    override val defaultFont: Font by lazy { LwjglStandardFont(Resource("res/fonts/Roboto-Regular.ttf")) {
        width = 1024
        height = 1024
        characterSize = 200f
    } }

    override fun font(res: Resource, initializer: FontAtlasConfiguration.() -> Unit) = LwjglStandardFont(res, initializer)
}
