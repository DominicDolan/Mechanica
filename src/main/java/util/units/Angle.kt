package util.units

interface Angle {
    fun toDouble(): Double
    fun toDegrees(): Degree
    fun toRadians(): Radian
}