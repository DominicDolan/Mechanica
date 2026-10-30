@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface
import platform.posix.*
import kotlin.test.*

class NativeAudioTest {
    @Test
    fun decodesAndUploadsOggWithoutPhysicalAudioDevice() {
        val fixture = listOf("samples/src/main/resources/res/audio/switch3.ogg", "../samples/src/main/resources/res/audio/switch3.ogg")
            .firstOrNull { access(it, F_OK) == 0 } ?: error("Missing sample audio fixture")
        val file = NativeOggAudioFile(NativeFileFactory(".", ".").resource(fixture))
        assertTrue(file.sampleRate > 0)
        assertTrue(file.channels in 1..2)
        assertTrue(file.samples.any { it != 0.toShort() })
        setenv("ALSOFT_DRIVERS", "null", 1)
        val factory = NativeAudioFactory()
        val context = NativeAudioContext(factory.listener())
        val config = object : ContextConfigurationData {
            override val display: Display? = null
            override var title = "test"
            override var resolutionWidth: Int? = null
            override var resolutionHeight: Int? = null
            override var viewWidth: Double? = null
            override var viewHeight: Double? = null
            override var viewX = 0.0
            override var viewY = 0.0
            override var fullscreen = false
            override var multisamplingSamples = 0
            override var surfaceConfiguration: DrawSurface.() -> Unit = {}
        }
        context.initialize(config)
        try {
            val sound = NativeSound(file)
            try {
                assertEquals(file.sampleRate, sound.frequency)
                val source = factory.source(sound)
                try {
                    source.looped = true
                    assertTrue(source.looped)
                    source.position.set(1.0, 2.0, 3.0)
                    assertEquals(2.0, source.position.y)
                    source.play()
                    assertTrue(source.isPlaying)
                } finally { source.destroy() }
            } finally { sound.destroy() }
        } finally { context.destroy() }
    }
}
