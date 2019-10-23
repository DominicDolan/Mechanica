package util.extensions

import util.units.Angle
import util.units.Degree
import util.units.Radian

inline val Number.degrees: Degree
    get() = Degree(this.toDouble())

inline val Number.radians: Radian
    get() = Radian(this.toDouble())

operator fun Angle.plus(other: Angle): Angle = (this.toRadians().toDouble() + other.toRadians().toDouble()).radians

operator fun Angle.minus(other: Angle): Angle = (this.toRadians().toDouble() - other.toRadians().toDouble()).radians

operator fun Angle.unaryMinus(): Angle = Radian(-(this.toRadians().toDouble()))

operator fun Degree.plus(other: Degree): Degree {
    return (this.toDouble() + other.toDouble()).degrees
}

operator fun Radian.plus(other: Radian): Radian {
    return (this.toDouble() + other.toDouble()).radians
}

operator fun Degree.minus(other: Degree): Degree {
    return (this.toDouble() - other.toDouble()).degrees
}

operator fun Radian.minus(other: Radian): Radian {
    return (this.toDouble() - other.toDouble()).radians
}

operator fun Degree.div(other: Degree): Double {
    return (this.toDouble() / other.toDouble())
}

operator fun Radian.div(other: Radian): Double {
    return (this.toDouble() / other.toDouble())
}

operator fun Degree.times(other: Number): Degree {
    return (this.toDouble() * other.toDouble()).degrees
}

operator fun Radian.times(other: Number): Radian {
    return (this.toDouble() * other.toDouble()).radians
}
