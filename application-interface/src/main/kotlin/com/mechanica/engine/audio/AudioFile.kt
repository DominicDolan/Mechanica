package com.mechanica.engine.audio

interface AudioFile {
    val channels: Int
    val sampleRate: Int
    val samples: ShortArray
    val format: Int
}