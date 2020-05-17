package com.mechanica.engine.shader.vars

import com.mechanica.engine.shader.qualifiers.Qualifier

interface ShaderVariable {
    val name: String
    val qualifier: Qualifier
    val type: ShaderType

    val declaration: String
        get() = "$qualifier $type $name; \n"

    override fun toString(): String

    companion object {
        fun create(name: String, qualifier: Qualifier, type: ShaderType) = object : ShaderVariable {
            override val name = name
            override val qualifier = qualifier
            override val type = type

            override fun toString() = name

        }
    }
}