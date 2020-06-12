package com.mechanica.engine.unit.angle

interface Angle {
    fun toDouble(): Double
    fun toFloat(): Float
    fun toDegrees(): Degree
    fun toRadians(): Radian
}