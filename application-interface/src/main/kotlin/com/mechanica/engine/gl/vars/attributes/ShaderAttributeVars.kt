package com.mechanica.engine.gl.vars.attributes

import com.mechanica.engine.gl.vbo.FloatAttributeType

interface ShaderAttributeVars {
    fun float(name: String? = null): FloatAttributeType

    fun vec2(name: String? = null): FloatAttributeType

    fun vec3(name: String? = null): FloatAttributeType

    fun vec4(name: String? = null): FloatAttributeType

    fun mat4(name: String? = null): FloatAttributeType

}