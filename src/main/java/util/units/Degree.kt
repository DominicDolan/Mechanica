package util.units

import util.extensions.radians
import kotlin.math.PI

inline class Degree(private val degrees: Double) : Angle {
    override fun toDegrees() = this

    override fun toRadians() = (this.toDouble()*PI /180.0).radians

    override fun toDouble() = degrees

    override fun toString(): String {
        return degrees.toString()
    }
}