package com.mechanica.engine.context

import com.mechanica.engine.audio.ALDevice
import com.mechanica.engine.audio.ALListener
import com.mechanica.engine.audio.AudioDevice
import com.mechanica.engine.audio.Listener
import com.mechanica.engine.configuration.ContextConfigurationData
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10
import java.nio.IntBuffer

class ALContext : AudioContext {
    val id: Long by lazy { createContext() }

    private var parsedVersionString: VersionStringParser? = null
    override val majorVersion: Int
        get() = parsedVersionString?.majorVersion ?: throw uninitializedException()
    override val minorVersion: Int
        get() = parsedVersionString?.minorVersion ?: throw uninitializedException()
    override val version: String
        get() = parsedVersionString?.version?.toString() ?: throw uninitializedException()

    override val listener: Listener by lazy { ALListener() }
    override val device: AudioDevice by lazy { ALDevice() }

    override fun initialize(data: ContextConfigurationData) {
        val capabilities = ALC.createCapabilities(device.id)

        ALC10.alcMakeContextCurrent(id)

        AL.createCapabilities(capabilities)

        parsedVersionString = parseVersionString()
    }

    override fun destroy() {
        ALC10.alcDestroyContext(id)
        ALC10.alcCloseDevice(device.id)
    }

    private fun createContext(): Long {
        val attrs: IntBuffer? = null
        return ALC10.alcCreateContext(device.id, attrs)
    }


    private fun parseVersionString(): VersionStringParser {
        val versionString: String = AL10.alGetString(AL10.AL_VERSION) ?:
        throw IllegalStateException("Unable to get the version of OpenAL")
        return VersionStringParser(versionString)
    }

    companion object {
        private const val uninitializedPropertyAccessMessage =
                "The OpenAL context has not yet been initialized. initialize(ContextConfigurationData) has to be called first"

        fun uninitializedException() = UninitializedPropertyAccessException(uninitializedPropertyAccessMessage)
    }
}