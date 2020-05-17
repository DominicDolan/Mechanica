package com.mechanica.engine.context.loader

import com.mechanica.engine.text.LwjglFont
import com.mechanica.engine.text.Font
import com.mechanica.engine.context.loader.FontLoader
import com.mechanica.engine.resources.Resource

class LwjglFontLoader : FontLoader {
    override val defaultFont: Font by lazy { LwjglFont(Resource("res/fonts/Roboto-Regular.ttf")) }

    override fun font(res: Resource) = LwjglFont(res)
}
