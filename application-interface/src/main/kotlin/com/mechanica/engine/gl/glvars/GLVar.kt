package com.mechanica.engine.gl.glvars

abstract class GLVar<T> {
    abstract val value: T
    abstract val name: String
    abstract val qualifier: String
    abstract val type: String
    abstract fun loadUniform()

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