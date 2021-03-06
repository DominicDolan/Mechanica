package com.mechanica.engine.audio

import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.EXTOffset.AL_SAMPLE_OFFSET
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

class ALSource(override var sound: Sound) : ALAudioObject(), SoundSource {
    override val id: Int = alGenSources()

    private val properties = ALSourceProperty(id)

    override var gain: Float by properties.createProperty(AL_GAIN)
    override var pitch: Float by properties.createProperty(AL_PITCH)
    override var rolloff: Float by properties.createProperty(AL_ROLLOFF_FACTOR)
    override var maxDistance: Float by properties.createProperty(AL_MAX_DISTANCE)
    override var referenceDistance: Float by properties.createProperty(AL_REFERENCE_DISTANCE)
    override var looped: Boolean
        get() = alGetSourcei(id, AL_LOOPING) == 1
        set(value) {
            val boolean = if (value) 1 else 0
            alSourcei(id, AL_LOOPING, boolean)
        }

    override val isPlaying: Boolean
        get() {
            val state = alGetSourcei(id, AL_SOURCE_STATE)
            return state == AL_PLAYING
        }
    override var progress: Float
        get() = alGetSourcei(id, AL_SAMPLE_OFFSET).toFloat()/sound.sampleRate.toFloat()
        set(value) {
            alSourcei(id, AL_SAMPLE_OFFSET, (value*sound.sampleRate).roundToInt())
        }

    private var isDeleted = false

    init {
        alSourcei(id, AL_BUFFER, sound.id)
    }

    override fun play() {
        if (!isDeleted) alSourcePlay(id)
    }

    override fun pause() {
        if (!isDeleted) alSourcePause(id)
    }

    override fun stop() {
        if (!isDeleted) alSourceStop(id)
    }

    override fun destroy() {
        if (!isDeleted) {
            isDeleted = true
            alSourcei(id, AL_BUFFER, 0)
            alDeleteSources(id)
        }
    }

    protected fun finalize() {
        destroy()
    }

    override fun getVector3(vec: ALVector3) {
        alGetSourcefv(id, vec.property, vec.array)
    }

    override fun setVector3(vec: ALVector3) {
        alSourcefv(id, vec.property, vec.array)
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