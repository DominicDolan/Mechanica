package com.mechanica.engine.audio

import com.mechanica.engine.resources.Resource
import kotlin.test.Test
import kotlin.test.assertTrue

class OggAudioFileTest {
    @Test
    fun decodesSamplesWithoutAnAudioDevice() {
        val bytes = requireNotNull(javaClass.getResourceAsStream("/res/audio/switch3.ogg")).use { it.readBytes() }
        val resource = object : Resource {
            override val path = "switch3.ogg"
            override fun readBytes() = bytes
        }
        val audio = OggAudioFile(resource)
        assertTrue(audio.channels in 1..2)
        assertTrue(audio.sampleRate > 0)
        assertTrue(audio.samples.isNotEmpty())
        assertTrue(audio.samples.any { it != 0.toShort() })
    }
}
