package com.mechanica.engine.shader.vars.attributes.vars

import com.mechanica.engine.shader.vars.ShaderType


interface AttributeType : ShaderType {
    val coordinateSize: Int
    companion object {

        fun create(coordinateSize: Int, name: String = getNameFromCoordinateSize(coordinateSize)): AttributeType {
            return object : AttributeType {
                override val type = name
                override val coordinateSize = coordinateSize
                override fun toString() = type
            }
        }

        fun float() = create(1)
        fun vec2() = create(2)
        fun vec3() = create(3)
        fun vec4() = create(4)
        fun mat4() = create(16)

        private fun getNameFromCoordinateSize(coordinateSize: Int): String {
            return when(coordinateSize) {
                1 -> "float"
                2 -> "vec2"
                3 -> "vec3"
                4 -> "vec4"
                9 -> "mat3"
                16 -> "mat4"
                else -> "[type undefined]"
            }
        }
    }
}
