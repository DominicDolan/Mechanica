package com.mechanica.engine.audio

import org.lwjgl.openal.ALC10
import java.nio.ByteBuffer

class ALDevice : AudioDevice {

    override val id: Long = getDefaultDevice()


    private fun getDefaultDevice(): Long {
        val specifier: ByteBuffer? = null
        return ALC10.alcOpenDevice(specifier)
    }
}