package shader.glvars

abstract class GLVar<T> {
    abstract var value: T
    abstract val name: String
    abstract val firstType: String
    abstract val secondType: String

    var location = 0

    val declaration: String
        get() = "$firstType $secondType $name; \n"

    override fun toString() = name
}