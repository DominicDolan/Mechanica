package gl.glvars

class GLFloat(
        override var value: Float,
        override val name: String,
        override val qualifier: String
) : GLVar<Float>() {
    override val type: String = "float"
}