package com.mechanica.engine.context.loader

import com.mechanica.engine.context.MechanicaInitializer
import com.mechanica.engine.shaders.context.ShaderLoader

interface MechanicaLoader {

    val constants: GLConstants
    val fileLoader: FileLoader
    val audioLoader: AudioLoader
    val inputLoader: InputLoader
    val shaderLoader: ShaderLoader

    val miscLoader: MiscLoader

    companion object : MechanicaLoader by MechanicaInitializer.loader
}