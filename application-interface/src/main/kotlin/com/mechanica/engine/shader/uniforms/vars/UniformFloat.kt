package com.mechanica.engine.shader.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class UniformFloat(
        override var value: Float,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Float>() {
    override val type = ShaderType.float()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Float) {
        this.value = value
    }
}