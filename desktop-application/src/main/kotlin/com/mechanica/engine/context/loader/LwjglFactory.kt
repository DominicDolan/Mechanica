package com.mechanica.engine.context.loader

class LwjglFactory : MechanicaFactory {
    override val constants = LwjglConstants()
    override val fileFactory = LwjglFileFactory()
    override val audioFactory = LwjglAudioFactory()
    override val inputFactory = LwjglInputFactory()
    override val miscFactory = LwjglMiscFactory()
    override val stencilFactory = LwjglStencilFactory()
    override val scissorFactory = LwjglScissorFactory()
    override val shaderFactory = LwjglShaderFactory()
}