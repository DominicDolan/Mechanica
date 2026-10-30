package com.mechanica.nativebackend

import com.mechanica.engine.context.Application
import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.shaders.text.FontFactory

/** Native application backed by unpacked assets. */
class NativeApplication(
    fontFactory: FontFactory = NativeFontFactory(),
    assetRoot: String,
    persistenceRoot: String,
    persistencePath: String,
) : Application {
    private val audioFactory = NativeAudioFactory()
    private val factory = object : MechanicaFactory {
        override val constants = NativeConstants()
        override val fileFactory = NativeFileFactory(assetRoot, persistenceRoot)
        override val audioFactory = this@NativeApplication.audioFactory
        override val inputFactory = NativeInputFactory()
        override val shaderFactory = NativeShaderFactory(fontFactory)
        override val stencilFactory = NativeStencilFactory()
        override val scissorFactory = NativeScissorFactory()
        override val miscFactory = NativeMiscFactory(persistencePath)
    }
    override val surfaceContext = NativeSurfaceContext()
    override val glContext = NativeOpenGLContext(surfaceContext)
    override val audioContext = NativeAudioContext(audioFactory.listener())
    override fun createFactory(): MechanicaFactory = factory

    override fun terminate() {
        try { audioContext.destroy() }
        finally {
            try { glContext.destroy() }
            finally { surfaceContext.destroy() }
        }
    }
}
