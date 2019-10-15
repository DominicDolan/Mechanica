package util.units

import util.extensions.degrees
import util.extensions.radians
import kotlin.math.PI

inline class Degree(private val degrees: Double) : Angle {
    override fun toDegrees() = toDouble().degrees

    override fun toRadians() = (this.toDouble()*PI /180.0).radians

    override fun toDouble() = (degrees+360.0)%360.0

    override fun toString(): String {
        return toDouble().toString()
    }
}