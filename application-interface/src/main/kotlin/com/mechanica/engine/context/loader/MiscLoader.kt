package com.mechanica.engine.context.loader

interface MiscLoader {
    fun prepareStencilForPath()
    fun stencilFunction()
    fun clearStencil()
    fun enableAlphaBlending()
}