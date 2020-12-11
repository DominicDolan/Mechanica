package com.mechanica.engine.context

import com.mechanica.engine.context.loader.LwjglLoader

class DesktopApplication : Application {
    override val surfaceContext: SurfaceContext = GLFWContext()
    override val glContext: OpenGLContext = GL40Context(this)

    override fun createLoader() = LwjglLoader()

    override fun terminate() {
        surfaceContext.destroy()
        glContext.destroy()
        ALContext.destroy()
    }

}