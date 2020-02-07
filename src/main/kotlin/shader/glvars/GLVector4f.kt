package shader.glvars

import util.colors.Color

class GLVector4f (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String,
        override val firstType: String,
        override val secondType: String
) : GLVar<FloatArray>() {
    override var value: FloatArray = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

    fun set(color: Color) {
        value[0] = color.r.toFloat()
        value[1] = color.g.toFloat()
        value[2] = color.b.toFloat()
        value[3] = color.a.toFloat()
    }

    fun set(x: Number, y: Number, z: Number, w: Number) {
        value[0] = x.toFloat()
        value[1] = y.toFloat()
        value[2] = z.toFloat()
        value[3] = w.toFloat()
    }

}