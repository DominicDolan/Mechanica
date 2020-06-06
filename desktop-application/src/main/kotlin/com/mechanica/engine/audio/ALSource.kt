package com.mechanica.engine.audio

import com.mechanica.engine.memory.useMemoryStack
import org.joml.Vector3f
import org.lwjgl.openal.AL10.*
import kotlin.reflect.KProperty

class ALSource(override val sound: Sound) : ALAudioObject(), SoundSource {
    override val id: Int = alGenSources()

    override var gain: Float by ALFloatProperty(AL_GAIN)
    override var pitch: Float by ALFloatProperty(AL_PITCH)
    override var rolloff: Float by ALFloatProperty(AL_ROLLOFF_FACTOR)
    override var maxDistance: Float by ALFloatProperty(AL_MAX_DISTANCE)
    override var referenceDistance: Float by ALFloatProperty(AL_REFERENCE_DISTANCE)

    init {
        alSourcei(id, AL_BUFFER, sound.id)
    }

    override fun play() {
        alSourcePlay(id)
    }

    override fun pause() {
        alSourcePause(id)
    }

    override fun stop() {
        alSourceStop(id)
    }

    override fun destroy() {
        alDeleteSources(id)
    }


    override fun getVector3f(property: Int, vec: Vector3f) {
        useMemoryStack {
            val floats = floats(0f, 0f, 0f)
            alGetSourcefv(id, property, floats)
            vec.set(floats[0], floats[1], floats[2])
        }
    }

    override fun set3f(property: Int, x: Float, y: Float, z: Float) {
        alSource3f(id, property, x, y, z)
    }


    private inner class ALFloatProperty(private val property: Int) {
        operator fun getValue(thisRef: SoundSource, property: KProperty<*>): Float {
            return getFloat(this.property)
        }

        operator fun setValue(thisRef: SoundSource, property: KProperty<*>, value: Float) {
            setFloat(this.property, value)
        }

        private fun getFloat(property: Int) = alGetSourcef(id, property)
        private fun setFloat(property: Int, float: Float) = alSourcef(id, property, float)
    }
}