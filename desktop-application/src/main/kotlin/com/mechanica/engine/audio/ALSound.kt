package com.mechanica.engine.audio

import org.lwjgl.openal.AL10.*

class ALSound(file: AudioFile) : Sound, AudioFile by file {
    override val id: Int = generateBufferId()
    override val frequency: Int
        get() = alGetBufferi(id, AL_FREQUENCY)

    constructor(file: String) : this(OggAudioFile(file))

    init {
        alBufferData(id, format, buffer, sampleRate)
    }

    override fun destroy() {
        alDeleteBuffers(id)
    }

    companion object {
        fun generateBufferId(): Int {
            alGetError()
            val id = alGenBuffers()
            val error = alGetError()
            if (error != AL_NO_ERROR) {
                throw IllegalStateException("Error generating OpenAL buffer, ERROR_CODE: $error")
            }
            return id
        }
    }
}