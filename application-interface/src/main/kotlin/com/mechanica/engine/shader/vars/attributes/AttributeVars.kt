package com.mechanica.engine.shader.vars.attributes

interface AttributeVars {
    fun float(name: String? = null): AttributeDefinition

    fun vec2(name: String? = null): AttributeDefinition

    fun vec3(name: String? = null): AttributeDefinition

    fun vec4(name: String? = null): AttributeDefinition

    fun mat4(name: String? = null): AttributeDefinition

}