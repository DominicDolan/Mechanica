package com.mechanica.engine.context

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface

interface Context : Version {
    /**
     * Initialize this context based on the Configuration Data given by [data]
     */
    fun initialize(data: ContextConfigurationData)
    fun destroy()
}

interface SurfaceContext : Context {
    val display: Display
    val surface: DrawSurface

    /**
     * Create a new surface context which has a shared context with this one. [activate] should be called
     * on the returned context (usually in another thread) before using it
     */
    fun createSharedContext(): SurfaceContext
    fun setCallbacks(callbacks: EventCallbacks)

    /**
     * Activate this context if it is not already activated. Typically this is not necessary for
     * the main context or for contexts in which [initialize] was called but it is usually
     * necessary for shared contexts
     */
    fun activate()
}

interface OpenGLContext : Context {
    fun startFrame()
}