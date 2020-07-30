package com.mechanica.engine.audio

import com.mechanica.engine.context.loader.GLLoader
import org.joml.Vector3f

interface Listener : AudioObject {
    var distanceModel: DistanceModel
    val orientation: Orientaion

    interface Orientaion {
        var at: Vector3f
        var up: Vector3f
    }

    companion object : Listener by GLLoader.audioLoader.listener()

    enum class DistanceModel {
        INVERSE,
        LINEAR,
        EXPONENTIAL;

        var clamped: Boolean = false
    }
}