package com.mechanica.engine.unit.angle

import kotlin.math.PI

inline class Degree(private val degrees: Double) : Angle {
    override fun toDegrees() = toDouble().degrees

    override fun toRadians() = (this.toDouble()*PI /180.0).radians

    override fun toDouble() = degrees

    override fun toFloat() = degrees.toFloat()

    override fun toString(): String {
        return toDouble().toString()
    }
}