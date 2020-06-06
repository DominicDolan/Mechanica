package com.mechanica.engine.context.loader

import com.mechanica.engine.audio.Listener
import com.mechanica.engine.audio.Sound
import com.mechanica.engine.audio.SoundSource

interface AudioLoader {
    fun sound(file: String): Sound
    fun source(sound: Sound): SoundSource
    fun listener(): Listener
}