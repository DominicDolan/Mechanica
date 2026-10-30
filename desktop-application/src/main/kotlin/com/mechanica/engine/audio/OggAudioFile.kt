package com.mechanica.engine.audio

import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.utils.toDirectBuffer
import org.lwjgl.openal.AL10
import org.lwjgl.stb.STBVorbis
import org.lwjgl.system.libc.LibCStdlib
import java.nio.ShortBuffer

class OggAudioFile(res: Resource) : AudioFile {
    override val channels: Int
    override val sampleRate: Int
    override val samples: ShortArray
    override val format: Int
        get() = when (channels) {
            1 -> AL10.AL_FORMAT_MONO16
            2 -> AL10.AL_FORMAT_STEREO16
            else -> -1
        }

    init {
        var channels = -1
        var sampleRate = -1
        var ogg: ShortBuffer? = null
        useMemoryStack {
            val channelBuffer = ints(0)
            val sampleRateBuffer = ints(0)
            ogg = STBVorbis.stb_vorbis_decode_memory(res.readBytes().toDirectBuffer(), channelBuffer, sampleRateBuffer)
            channels = channelBuffer[0]
            sampleRate = sampleRateBuffer[0]
        }

        this.samples = copySamples(ogg) ?: throw IllegalStateException("Unable to load audio file: ${res.path}")

        this.channels = channels
        this.sampleRate = sampleRate
    }

    private fun copySamples(ogg: ShortBuffer?): ShortArray? {
        if (ogg == null) return null
        return try {
            ShortArray(ogg.remaining()).also { ogg.get(it) }
        } finally {
            ogg.position(0)
            LibCStdlib.free(ogg)
        }
    }
}
