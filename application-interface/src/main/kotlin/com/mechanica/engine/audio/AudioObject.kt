package com.mechanica.engine.audio

import com.cave.library.vector.vec3.MutableVector3

interface AudioObject {
    var gain: Float
    var position: MutableVector3
    var velocity: MutableVector3
}