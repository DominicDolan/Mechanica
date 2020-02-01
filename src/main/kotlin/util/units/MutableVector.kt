package util.units

class MutableVector(override var x: Double, override var y: Double) : Vector {
    fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun set(other: Vector) {
        this.x = other.x
        this.y = other.y
    }
}