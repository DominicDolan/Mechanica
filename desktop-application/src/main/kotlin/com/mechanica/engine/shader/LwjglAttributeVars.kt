package com.mechanica.engine.shader

import com.mechanica.engine.shaders.attributes.*
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier


class LwjglAttributeVars(private val qualifier: AttributeQualifier) : AttributeVars {
    override fun float(name: String?): AttributeFloat {
        return object : AttributeFloat(name ?: "") {
            override fun loadAttribute() { }
            override val qualifier = this@LwjglAttributeVars.qualifier
        }
    }

    override fun vec2(name: String?): AttributeVector2 {
        return object : AttributeVector2(name ?: "") {
            override fun loadAttribute() { }
            override val qualifier = this@LwjglAttributeVars.qualifier
        }
    }

    override fun vec3(name: String?): AttributeVector3 {
        return object : AttributeVector3(name ?: "") {
            override fun loadAttribute() { }
            override val qualifier = this@LwjglAttributeVars.qualifier
        }
    }

    override fun vec4(name: String?): AttributeVector4 {
        return object : AttributeVector4(name ?: "") {
            override fun loadAttribute() { }
            override val qualifier = this@LwjglAttributeVars.qualifier
        }
    }

    override fun mat4(name: String?): AttributeMatrix4 {
        return object : AttributeMatrix4(name ?: "") {
            override fun loadAttribute() { }
            override val qualifier = this@LwjglAttributeVars.qualifier
        }
    }
}
