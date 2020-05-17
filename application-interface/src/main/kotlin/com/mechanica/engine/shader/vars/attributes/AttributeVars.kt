package com.mechanica.engine.shader.vars.attributes

import com.mechanica.engine.vertices.FloatBufferMaker

interface AttributeVars {
    fun float(name: String? = null): FloatBufferMaker

    fun vec2(name: String? = null): FloatBufferMaker

    fun vec3(name: String? = null): FloatBufferMaker

    fun vec4(name: String? = null): FloatBufferMaker

    fun mat4(name: String? = null): FloatBufferMaker

}