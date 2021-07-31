package com.mechanica.engine.resources

import com.mechanica.engine.audio.AudioFile
import com.mechanica.engine.audio.Sound
import com.mechanica.engine.audio.SoundSource
import com.mechanica.engine.context.loader.MechanicaFactory
import java.nio.ShortBuffer

class AudioResource(res: Resource, val sound: Sound) : GenericResource by res, AudioFile by sound {

    constructor(res: Resource) : this(res, MechanicaFactory.audioFactory.sound(res))
    constructor(file: String) : this(Resource(file))

    override val buffer: ShortBuffer
        get() = sound.buffer

    fun createSource(): SoundSource = MechanicaFactory.audioFactory.source(sound)
}