package com.mechanica.engine.unit.angle

inline val Number.degrees: Degree
    get() = Degree(this.toDouble())

inline val Number.radians: Radian
    get() = Radian(this.toDouble())

operator fun Angle.plus(other: Angle): Angle = (this.toRadians().asDouble() + other.toRadians().asDouble()).radians

operator fun Angle.minus(other: Angle): Angle = (this.toRadians().asDouble() - other.toRadians().asDouble()).radians

operator fun Angle.unaryMinus(): Angle = Radian(-(this.toRadians().asDouble()))

operator fun Degree.plus(other: Degree): Degree {
    return (this.asDouble() + other.asDouble()).degrees
}

operator fun Radian.plus(other: Radian): Radian {
    return (this.asDouble() + other.asDouble()).radians
}

operator fun Degree.minus(other: Degree): Degree {
    return (this.asDouble() - other.asDouble()).degrees
}

operator fun Radian.minus(other: Radian): Radian {
    return (this.asDouble() - other.asDouble()).radians
}

operator fun Degree.div(other: Degree): Double {
    return (this.asDouble() / other.asDouble())
}

operator fun Radian.div(other: Radian): Double {
    return (this.asDouble() / other.asDouble())
}

operator fun Degree.times(other: Number): Degree {
    return (this.asDouble() * other.toDouble()).degrees
}

operator fun Radian.times(other: Number): Radian {
    return (this.asDouble() * other.toDouble()).radians
}
