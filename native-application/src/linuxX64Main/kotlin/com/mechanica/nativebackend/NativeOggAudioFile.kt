@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.audio.AudioFile
import com.mechanica.engine.resources.Resource
import com.mechanica.nativebackend.openal.AL_FORMAT_MONO16
import com.mechanica.nativebackend.openal.AL_FORMAT_STEREO16
import com.mechanica.nativebackend.stb.stb_vorbis_decode_memory
import kotlinx.cinterop.*
import platform.posix.free

/** Decoded interleaved signed 16-bit PCM. No OpenAL device is needed for decoding. */
class NativeOggAudioFile(resource: Resource) : AudioFile {
    override val channels: Int
    override val sampleRate: Int
    override val samples: ShortArray
    override val format: Int
        get() = when (channels) {
            1 -> AL_FORMAT_MONO16
            2 -> AL_FORMAT_STEREO16
            else -> error("Unsupported channel count: $channels")
        }

    init {
        val encoded = resource.readBytes()
        require(encoded.isNotEmpty()) { "Empty audio resource: ${resource.path}" }
        val decoded = memScoped {
            val channelCount = alloc<IntVar>()
            val rate = alloc<IntVar>()
            val output = alloc<CPointerVar<ShortVar>>()
            val frameCount = encoded.usePinned {
                stb_vorbis_decode_memory(it.addressOf(0).reinterpret(), encoded.size, channelCount.ptr, rate.ptr, output.ptr)
            }
            val pointer = output.value
            try {
                check(frameCount >= 0 && pointer != null) { "Unable to decode audio: ${resource.path}" }
                check(channelCount.value in 1..2 && rate.value > 0) { "Unsupported audio format: ${resource.path}" }
                val count = frameCount.toLong() * channelCount.value
                check(count <= Int.MAX_VALUE) { "Audio resource is too large: ${resource.path}" }
                Triple(channelCount.value, rate.value, ShortArray(count.toInt()) { pointer[it] })
            } finally { if (pointer != null) free(pointer) }
        }
        channels = decoded.first
        sampleRate = decoded.second
        samples = decoded.third
    }
}
