package com.mechanica.engine.shader.vars

interface ShaderType {
    val type: String
    override fun toString(): String
    companion object {
        fun get(name: String): ShaderType {
            return object : ShaderType {
                override val type = name
                override fun toString() = type
            }
        }

        fun float() = get("float")
        fun vec2() = get("vec2")
        fun vec3() = get("vec3")
        fun vec4() = get("vec4")
        fun mat4() = get("mat4")
    }
}