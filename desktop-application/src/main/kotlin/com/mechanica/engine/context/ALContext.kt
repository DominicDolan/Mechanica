package com.mechanica.engine.context

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import java.nio.ByteBuffer
import java.nio.IntBuffer

object ALContext {
    val device by lazy { getDefaultDevice() }
    val context by lazy { createContext() }

    fun initialize() {
        val capabilities = ALC.createCapabilities(device)

        alcMakeContextCurrent(context)

        AL.createCapabilities(capabilities)
    }

    fun destroy() {
        alcDestroyContext(context)
        alcCloseDevice(device)
    }

    private fun getDefaultDevice(): Long {
        val specifier: ByteBuffer? = null
        return alcOpenDevice(specifier)
    }

    private fun createContext(): Long {
        val attrs: IntBuffer? = null
        return alcCreateContext(device, attrs)
    }
}