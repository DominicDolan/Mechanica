package com.mechanica.engine.context.loader

import com.mechanica.engine.context.MechanicaInitializer
import com.mechanica.engine.shaders.context.ShaderFactory

interface MechanicaFactory {

    val constants: GLConstants
    val fileFactory: FileFactory
    val audioFactory: AudioFactory
    val inputFactory: InputFactory
    val shaderFactory: ShaderFactory

    val miscFactory: MiscFactory

    companion object : MechanicaFactory by MechanicaInitializer.factory
}