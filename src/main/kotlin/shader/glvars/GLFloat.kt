package shader.glvars

class GLFloat(
        override var value: Float,
        override val name: String,
        override val firstType: String,
        override val secondType: String
) : GLVar<Float>()