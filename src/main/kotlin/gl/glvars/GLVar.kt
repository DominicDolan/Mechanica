package gl.glvars

fun main() {
    val name = "some_N4me32[20]"
    val regex = Regex("[^\\w\\d]")
    val index = regex.find(name)?.range?.first
    val subName = if (index != null) {
        name.substring(0 until index)
    } else name
    println(subName)
    name.matches(regex)
}

abstract class GLVar<T> {
    abstract var value: T
    abstract val name: String
    abstract val qualifier: String
    abstract val type: String

    var location = 0

    private val regex = Regex("[^\\w\\d]")
    val locationName: String
        get() {
            val index = regex.find(name)?.range?.first
            return if (index != null) {
                name.substring(0 until index)
            } else name
        }

    val declaration: String
        get() = "$qualifier $type $name; \n"

    override fun toString() = name
}