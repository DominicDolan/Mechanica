package com.mechanica.engine.shader.qualifiers

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.shader.script.ScriptVariables
import com.mechanica.engine.shader.vars.ShaderVariableDefinition
import com.mechanica.engine.shader.vars.attributes.AttributeDefinition
import com.mechanica.engine.shader.vars.attributes.AttributeVars
import com.mechanica.engine.shader.vars.attributes.vars.AttributeType

class Attribute internal constructor(
        override val location: Int,
        private val variables: ScriptVariables? = null) : AttributeQualifier, AttributeVars {

    internal val loader = GLLoader.createAttributeLoader(this)

    override fun float(name: String?): AttributeDefinition {
        return createFloatAttribute("float", name, 1)
    }

    override fun vec2(name: String?): AttributeDefinition {
        return createFloatAttribute("vec2", name, 2)
    }

    override fun vec3(name: String?): AttributeDefinition {
        return createFloatAttribute("vec3", name, 3)
    }

    override fun vec4(name: String?): AttributeDefinition {
        return createFloatAttribute("vec4", name, 4)
    }

    override fun mat4(name: String?): AttributeDefinition {
        return createFloatAttribute("mat4", name, 16)
    }

    override fun toString() = qualifierName

    private fun createFloatAttribute(typeName: String, name: String?, coordinateSize: Int): AttributeDefinition {
        val variableName = name ?: variables?.getNextName() ?: ""
        val type = AttributeType.create(coordinateSize, typeName)
        val variable = object : AttributeDefinition {
            override val qualifier = this@Attribute
            override val type = type
            override val name: String = variableName

            override fun toString() = this.name
        }

        return addVariable(variable)
    }

    private inline fun <reified T : ShaderVariableDefinition> addVariable(v: T): T {
        val new = variables?.addVariable(v) ?: v
        return try {
            new as T
        } catch (ex: ClassCastException) {
            val message = "A variable in the shader already exists with the name ${v.name} and it has a different type\n" +
                    "The existing variables type is ${new.type} and it can't be cast to ${v.type}"
            throw ClassCastException(message)
        }
    }

    companion object {
        private val attributeLocations = Array<Attribute?>(20) { null}

        val position: AttributeDefinition
            get() = location(0).vec3()
        val textureCoords: AttributeDefinition
            get() = location(1).vec2()

        fun location(location: Int): AttributeVars {
            return if (location > attributeLocations.lastIndex) {
                Attribute(location)
            } else {
                val nullable = attributeLocations[location]
                if (nullable == null) {
                    val attribute = Attribute(location)
                    attributeLocations[location] = attribute
                    attribute
                } else nullable
            }
        }
    }
}