package com.mechanica.engine.audio

import com.cave.library.vector.vec3.Vector3
import com.mechanica.engine.context.loader.MechanicaLoader

interface Listener : AudioObject {
    var distanceModel: DistanceModel
    val orientation: Orientaion

    interface Orientaion {
        var at: Vector3
        var up: Vector3
    }

    companion object : Listener by MechanicaLoader.audioLoader.listener()

    enum class DistanceModel {
        INVERSE,
        LINEAR,
        EXPONENTIAL;

        var clamped: Boolean = false
    }
}