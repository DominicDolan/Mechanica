package com.mechanica.engine.unit.angle

interface Angle {
    fun asDouble(): Double
    fun toDegrees(): Degree
    fun toRadians(): Radian
}