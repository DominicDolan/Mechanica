package com.mechanica.engine.context.loader

interface MiscFactory {
    fun prepareStencilForPath()
    fun stencilFunction()
    fun clearStencil()
    fun enableAlphaBlending()
}