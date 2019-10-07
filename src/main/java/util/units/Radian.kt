package util.units

import util.extensions.degrees
import kotlin.math.PI

inline class Radian(private val radians: Double) : Angle {
    override fun toDegrees() = (this.toDouble()*180.0/ PI).degrees

    override fun toRadians() = this

    override fun toDouble() = radians

    override fun toString(): String {
        return radians.toString()
    }
}