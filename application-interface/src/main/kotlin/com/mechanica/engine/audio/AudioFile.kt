package com.mechanica.engine.audio

import java.nio.ShortBuffer

interface AudioFile {
    val channels: Int
    val sampleRate: Int
    val buffer: ShortBuffer
    val format: Int
}