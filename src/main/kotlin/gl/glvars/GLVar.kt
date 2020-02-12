package gl.glvars

abstract class GLVar<T> {
    abstract var value: T
    abstract val name: String
    abstract val qualifier: String
    abstract val type: String

    var location = 0

    val declaration: String
        get() = "$qualifier $type $name; \n"

    override fun toString() = name
}