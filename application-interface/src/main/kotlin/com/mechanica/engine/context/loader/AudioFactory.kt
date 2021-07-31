package com.mechanica.engine.context.loader

import com.mechanica.engine.audio.Listener
import com.mechanica.engine.audio.Sound
import com.mechanica.engine.audio.SoundSource
import com.mechanica.engine.resources.Resource

interface AudioFactory {
    fun sound(res: Resource): Sound
    fun source(sound: Sound): SoundSource
    fun listener(): Listener
}