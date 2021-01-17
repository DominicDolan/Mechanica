package com.mechanica.engine.context.loader

class LwjglLoader : MechanicaLoader {
    override val constants = LwjglConstants()
    override val bufferLoader = LwjglBufferLoader()
    override val fontLoader = LwjglFontLoader()
    override val fileLoader = LwjglFileLoader()
    override val graphicsLoader = LwjglGraphicsLoader()
    override val audioLoader = LwjglAudioLoader()
    override val inputLoader = LwjglInputLoader()
    override val shaderLoader = LwjglShaderLoader()
    override val miscLoader = LwjglMiscLoader()
    override val glPrimitives = LwjglPrimitiveLoader()
}