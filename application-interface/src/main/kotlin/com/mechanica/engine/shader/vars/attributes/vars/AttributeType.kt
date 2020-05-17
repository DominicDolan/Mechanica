package com.mechanica.engine.shader.vars.attributes.vars

import com.mechanica.engine.shader.vars.ShaderType


interface AttributeType : ShaderType {
    val coordinateSize: Int
    companion object {

        fun get(coordinateSize: Int, name: String): AttributeType {
            return object : AttributeType {
                override val type = name
                override val coordinateSize = coordinateSize
                override fun toString() = type
            }
        }

        fun float() = get(1, "float")
        fun vec2() = get(2, "vec2")
        fun vec3() = get(3, "vec3")
        fun vec4() = get(4, "vec4")
        fun mat4() = get(16, "mat4")
    }
}
