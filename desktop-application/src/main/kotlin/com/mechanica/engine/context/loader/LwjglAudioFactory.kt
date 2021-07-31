package com.mechanica.engine.context.loader

import com.mechanica.engine.audio.ALListener
import com.mechanica.engine.audio.ALSound
import com.mechanica.engine.audio.ALSource
import com.mechanica.engine.audio.Sound
import com.mechanica.engine.resources.Resource

class LwjglAudioFactory : AudioFactory {
    override fun sound(res: Resource): Sound = ALSound(res)

    override fun source(sound: Sound) = ALSource(sound)

    override fun listener() = ALListener()
}