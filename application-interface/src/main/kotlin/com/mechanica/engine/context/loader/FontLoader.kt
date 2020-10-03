package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.text.Font
import com.mechanica.engine.text.FontAtlasConfiguration

interface FontLoader {
    fun font(res: Resource, initializer: FontAtlasConfiguration.() -> Unit): Font

}