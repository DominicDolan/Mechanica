package com.mechanica.engine.context.loader

interface StencilFactory {
    fun prepareStencil(type: StencilType, ref: Int = 1, mask: Int = 0xFF)
    fun enableStencilWrite(write: Boolean = true)
    fun disableStencilTest()
}