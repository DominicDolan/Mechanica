package com.mechanica.engine.context.loader

import com.mechanica.engine.input.KeyIDs

interface InputLoader {
    fun keyIds(): KeyIDs
}