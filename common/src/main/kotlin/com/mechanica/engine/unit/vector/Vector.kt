package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.angle.radians
import kotlin.math.atan2
import kotlin.math.hypot

interface Vector {
    val x: Double
    val y: Double

    val r get() = hypot(x, y)

    val theta get() = atan2(this.y, this.x).radians

}