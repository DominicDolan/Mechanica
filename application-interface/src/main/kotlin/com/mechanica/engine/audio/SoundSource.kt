package com.mechanica.engine.audio

import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.resources.AudioResource

interface SoundSource : AudioObject {
    val id: Int
    val sound: Sound
    var pitch: Float
    var rolloff: Float
    var maxDistance: Float
    var referenceDistance: Float
    val isPlaying: Boolean
    var looped: Boolean
    var progress: Float
    fun play()
    fun pause()
    fun stop()
    fun destroy()

    companion object {
        fun create(res: AudioResource) = MechanicaFactory.audioFactory.source(res.sound)
    }

}