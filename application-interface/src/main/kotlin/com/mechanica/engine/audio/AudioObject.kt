package com.mechanica.engine.audio

import org.joml.Vector3f

interface AudioObject {
    var gain: Float
    var position: Vector3f
    var velocity: Vector3f
}