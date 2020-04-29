package com.mechanica.engine.unit.angle

import kotlin.math.PI

inline class Degree(private val degrees: Double) : Angle {
    override fun toDegrees() = asDouble().degrees

    override fun toRadians() = (this.asDouble()*PI /180.0).radians

    override fun asDouble() = degrees

    override fun toString(): String {
        return asDouble().toString()
    }
}