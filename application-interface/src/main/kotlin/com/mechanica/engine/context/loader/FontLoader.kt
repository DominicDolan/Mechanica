package com.mechanica.engine.context.loader

import com.mechanica.engine.text.Font
import com.mechanica.engine.resources.Resource

interface FontLoader {
    val defaultFont: Font
    fun font(res: Resource): Font

}