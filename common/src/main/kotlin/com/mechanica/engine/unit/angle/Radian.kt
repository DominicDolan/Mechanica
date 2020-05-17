package com.mechanica.engine.unit.angle

import kotlin.math.PI

inline class Radian(private val radians: Double) : Angle {
    override fun toDegrees() = (this.asDouble()*180.0/ PI).degrees

    override fun toRadians() = asDouble().radians

    override fun asDouble() = radians

    override fun toString(): String {
        return asDouble().toString()
    }
}