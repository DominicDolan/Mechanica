package shader.glvars

import util.units.Vector

class GLVector2f(
        x: Number, y: Number,
        override val name: String,
        override val firstType: String,
        override val secondType: String
) : GLVar<FloatArray>() {
    override var value: FloatArray = floatArrayOf(x.toFloat(), y.toFloat())

    fun set(x: Number, y: Number) {
        value[0] = x.toFloat()
        value[1] = y.toFloat()
    }

    fun set(vec: Vector) {
        value[0] = vec.x.toFloat()
        value[1] = vec.y.toFloat()
    }
}