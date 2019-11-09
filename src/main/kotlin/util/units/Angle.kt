package util.units

interface Angle {
    fun asDouble(): Double
    fun toDegrees(): Degree
    fun toRadians(): Radian
}