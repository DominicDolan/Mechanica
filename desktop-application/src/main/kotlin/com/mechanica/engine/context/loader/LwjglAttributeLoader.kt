package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.*
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.vars.GlslLocation
import org.lwjgl.opengl.GL20

class LwjglAttributeLoader : AttributeLoader {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            this.location = GL20.glGetAttribLocation(shader.id, locationName)
        }
    }

    override fun variables(qualifier: AttributeQualifier): AttributeVars {
        return LwjglAttributeVars(qualifier)
    }

    override fun disableAttributeArray(value: Int) {
        GL20.glDisableVertexAttribArray(value)
    }
}

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