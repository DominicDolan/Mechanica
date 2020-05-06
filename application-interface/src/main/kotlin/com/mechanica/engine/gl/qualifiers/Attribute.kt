package com.mechanica.engine.gl.qualifiers

import com.mechanica.engine.gl.context.loader.GLLoader
import com.mechanica.engine.gl.script.ScriptVariables
import com.mechanica.engine.gl.vars.attributes.AttributeVars
import com.mechanica.engine.gl.vars.attributes.ShaderAttributeVars
import com.mechanica.engine.gl.vbo.FloatAttributeType
import com.mechanica.engine.gl.vbo.ShaderFloatAttributeType

class Attribute(location: Int, private val variables: ScriptVariables) : Qualifier, ShaderAttributeVars {
    override val qualifierName: String = "layout (location=$location) in"

    private val loader = GLLoader.createAttributeLoader(location)

    override fun float(name: String?): FloatAttributeType {
        return createFloatAttribute("float", name, 1)
    }

    override fun vec2(name: String?): FloatAttributeType {
        return createFloatAttribute("vec2", name, 2)
    }

    override fun vec3(name: String?): FloatAttributeType {
        return createFloatAttribute("vec3", name, 3)
    }

    override fun vec4(name: String?): FloatAttributeType {
        return createFloatAttribute("vec4", name, 4)
    }

    override fun mat4(name: String?): FloatAttributeType {
        return createFloatAttribute("mat4", name, 16)
    }

    override fun toString() = qualifierName

    private fun createFloatAttribute(type: String, name: String?, coordinateSize: Int): ShaderFloatAttributeType {
        val variableName = name ?: variables.getNextName()
        val attribute = loader.createFloatAttribute(coordinateSize)
        val v = ShaderFloatAttributeType(attribute, variableName, this, type)
        return addFloatVariable(v)
    }

    private fun addFloatVariable(v: ShaderFloatAttributeType): ShaderFloatAttributeType {
        return try {
            variables.addVariable(v) as ShaderFloatAttributeType
        } catch (ex: ClassCastException) {
            val message = "A variable in the shader already exists with the name ${v.name} and it has a different type\n" +
                    "The existing variables type is ${variables.addVariable(v).type} and it can't be cast to ${v.type}"
            throw ClassCastException(message)
        }
    }

    companion object {
        operator fun invoke(location: Int): AttributeVars {
            val loader = GLLoader.createAttributeLoader(location)
            return object : AttributeVars {
                override fun float() = loader.createFloatAttribute(1)
                override fun vec2() = loader.createFloatAttribute(2)
                override fun vec3() = loader.createFloatAttribute(3)
                override fun vec4() = loader.createFloatAttribute(4)
                override fun mat4() = loader.createFloatAttribute(16)
            }
        }
    }
}