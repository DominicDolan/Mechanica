package com.mechanica.engine.context.loader

import com.mechanica.engine.input.KeyIDs

interface InputFactory {
    fun keyIds(): KeyIDs
}