package com.mechanica.engine.context.loader

import com.mechanica.engine.audio.*

class LwjglAudioLoader : AudioLoader {
    override fun sound(file: String) = ALSound(file)

    override fun source(sound: Sound) = ALSource(sound)

    override fun listener() = ALListener()
}