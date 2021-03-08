package com.mechanica.engine.audio

import com.cave.library.vector.vec3.VariableVector3

interface AudioObject {
    var gain: Float
    var position: VariableVector3
    var velocity: VariableVector3
}