@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.audio.AudioFile
import com.mechanica.engine.audio.Sound
import com.mechanica.engine.resources.Resource
import com.mechanica.nativebackend.openal.*
import kotlinx.cinterop.*

/** An OpenAL buffer; requires a current OpenAL context. */
class NativeSound(private val file: AudioFile) : Sound, AudioFile by file {
    constructor(resource: Resource) : this(NativeOggAudioFile(resource))

    private var deleted = false
    override val id: Int
    override val frequency: Int
        get() = memScoped {
            val value = alloc<IntVar>()
            alGetBufferi(id.toUInt(), AL_FREQUENCY, value.ptr)
            value.value
        }

    init {
        id = memScoped {
            val buffer = alloc<UIntVar>()
            alGetError()
            alGenBuffers(1, buffer.ptr)
            check(buffer.value != 0u && alGetError() == AL_NO_ERROR) { "Unable to create audio buffer" }
            try {
                val data = file.samples
                require(data.size <= Int.MAX_VALUE / 2) { "Audio data is too large" }
                if (data.isEmpty()) alBufferData(buffer.value, file.format, null, 0, file.sampleRate)
                else data.usePinned { alBufferData(buffer.value, file.format, it.addressOf(0), data.size * 2, file.sampleRate) }
                check(alGetError() == AL_NO_ERROR) { "Unable to upload audio data" }
                buffer.value.toInt()
            } catch (error: Throwable) {
                alDeleteBuffers(1, buffer.ptr)
                throw error
            }
        }
    }

    override fun destroy() {
        if (!deleted) memScoped {
            deleted = true
            val buffer = alloc<UIntVar>(); buffer.value = id.toUInt()
            alDeleteBuffers(1, buffer.ptr)
        }
    }
}
