package com.mechanica.engine.gl.vars.attributes

import com.mechanica.engine.gl.vbo.FloatAttributeType

interface AttributeVars {
    fun float(): FloatAttributeType

    fun vec2(): FloatAttributeType

    fun vec3(): FloatAttributeType

    fun vec4(): FloatAttributeType

    fun mat4(): FloatAttributeType
}