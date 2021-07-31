package com.mechanica.engine.context.loader

import com.mechanica.engine.input.GLFWKeyIDs

class LwjglInputFactory : InputFactory {
    override fun keyIds() = GLFWKeyIDs()
}