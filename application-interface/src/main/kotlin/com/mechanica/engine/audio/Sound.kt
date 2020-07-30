package com.mechanica.engine.audio

interface Sound : AudioFile {
    val id: Int
    val frequency: Int
    fun destroy()
}