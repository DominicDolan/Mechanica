package util.units

import util.extensions.degrees
import util.extensions.radians
import kotlin.math.PI

inline class Radian(private val radians: Double) : Angle {
    override fun toDegrees() = (this.toDouble()*180.0/ PI).degrees

    override fun toRadians() = toDouble().radians

    override fun toDouble() = (radians+2*PI)

    override fun toString(): String {
        return toDouble().toString()
    }
}