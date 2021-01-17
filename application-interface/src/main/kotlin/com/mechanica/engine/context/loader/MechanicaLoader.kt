package com.mechanica.engine.context.loader

import com.mechanica.engine.context.MechanicaInitializer

interface MechanicaLoader {

    val constants: GLConstants
    val bufferLoader: BufferLoader
    val fontLoader: FontLoader
    val fileLoader: FileLoader
    val graphicsLoader: GraphicsLoader
    val audioLoader: AudioLoader
    val inputLoader: InputLoader
    val shaderLoader: ShaderLoader
    val glPrimitives: GLPrimitiveLoader

    val miscLoader: MiscLoader

    companion object : MechanicaLoader by MechanicaInitializer.loader
}