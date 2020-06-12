package com.mechanica.engine.unit.angle

import kotlin.math.PI

inline class Radian(private val radians: Double) : Angle {
    override fun toDegrees() = (this.toDouble()*180.0/ PI).degrees

    override fun toRadians() = toDouble().radians

    override fun toDouble() = radians

    override fun toFloat() = radians.toFloat()

    override fun toString(): String {
        return toDouble().toString()
    }
}