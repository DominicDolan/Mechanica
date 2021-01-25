package com.mechanica.engine.context.loader

class LwjglLoader : MechanicaLoader {
    override val constants = LwjglConstants()
    override val fileLoader = LwjglFileLoader()
    override val audioLoader = LwjglAudioLoader()
    override val inputLoader = LwjglInputLoader()
    override val miscLoader = LwjglMiscLoader()
    override val shaderLoader = LwjglShaderLoader()
}