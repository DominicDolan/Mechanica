package gl.glvars

class GLVector3f(
        x: Number, y: Number, z: Number,
        override val name: String,
        override val qualifier: String
) : GLVar<FloatArray>() {
    override var value: FloatArray = floatArrayOf(x.toFloat(), y.toFloat(), z.toFloat())
    override val type: String = "vec3"

    fun set(x: Number, y: Number, z: Number) {
        value[0] = x.toFloat()
        value[1] = y.toFloat()
        value[2] = z.toFloat()
    }
}