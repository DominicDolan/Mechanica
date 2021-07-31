package com.mechanica.engine.context

import com.mechanica.engine.context.loader.LwjglFactory

class DesktopApplication : Application {
    override val surfaceContext: SurfaceContext = GLFWContext()
    override val glContext: OpenGLContext = GL40Context(this)
    override val audioContext: AudioContext = ALContext()

    override fun createFactory() = LwjglFactory()

}