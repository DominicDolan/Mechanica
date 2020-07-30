package com.mechanica.engine.context.loader

import com.mechanica.engine.input.GLFWKeyIDs
import com.mechanica.engine.input.KeyIDs

class LwjglInputLoader : InputLoader {
    override fun keyIds() = GLFWKeyIDs()
}