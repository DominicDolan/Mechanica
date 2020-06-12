package com.mechanica.engine.shader.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.shader.vars.ShaderVariable
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class UniformVar<T> : ShaderVariable, ReadWriteProperty<Any, T> {
    abstract val value: T
    abstract override val name: String
    abstract override val qualifier: Qualifier
    abstract override val type: ShaderType
    abstract fun loadUniform()

    var location = 0

    val locationName: String
        get() = getLocationName(name)

    override fun toString() = name

    override fun getValue(thisRef: Any, property: KProperty<*>) = value

    companion object {
        private val regex = Regex("[^\\w\\d]")

        fun getLocationName(name: String): String {
            val index = regex.find(name)?.range?.first
            return if (index != null) {
                name.substring(0 until index)
            } else name
        }
    }
}