package com.mechanica.engine.shaders.attributes

import com.mechanica.engine.shaders.context.ShaderLoader
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier
import com.mechanica.engine.shaders.script.ScriptVariables

class Attribute internal constructor(
        private val variables: ScriptVariables? = null,
        qualifier: AttributeQualifier = Companion.qualifier,
) : AttributeVars {

    private val loader: AttributeVars = ShaderLoader.attributeLoader.variables(qualifier)

    override fun float(name: String?): AttributeFloat {
        val variableName = name ?: variables?.getNextName() ?: ""
        val v = loader.float(variableName)
        return addVariable(v)
    }

    override fun vec2(name: String?): AttributeVector2 {
        val variableName = name ?: variables?.getNextName() ?: ""
        val v = loader.vec2(variableName)
        return addVariable(v)
    }

    override fun vec3(name: String?): AttributeVector3 {
        val variableName = name ?: variables?.getNextName() ?: ""
        val v = loader.vec3(variableName)
        return addVariable(v)
    }

    override fun vec4(name: String?): AttributeVector4 {
        val variableName = name ?: variables?.getNextName() ?: ""
        val v = loader.vec4(variableName)
        return addVariable(v)
    }

    override fun mat4(name: String?): AttributeMatrix4 {
        TODO("Not implemented")
    }

    private inline fun <reified T : AttributeVar<*>> addVariable(v: T): T {
        val new = variables?.addVariable(v) ?: v

        return try {
            new as T
        } catch (ex: ClassCastException) {
            val message = "A variable in the shader already exists with the name ${v.name} and it has a different type\n" +
                    "The existing variables type is ${new.type} and it can't be cast to ${v.type}"
            throw ClassCastException(message)
        }
    }

    companion object : AttributeVars by Attribute(null, object : AttributeQualifier {override fun toString() = qualifierName }) {

        private val attributeLocations = Array<Attribute?>(20) { null}
        const val positionLocation: Int = 0
        const val texCoordsLocation: Int = 1

        val position: AttributeVector3
            get() = location(0).vec3()
        val textureCoords: AttributeVector2
            get() = location(1).vec2()

        fun location(location: Int): AttributeVars {
            return if (location > attributeLocations.lastIndex) {
                Attribute(qualifier = qualifier(location))
            } else {
                val nullable = attributeLocations[location]
                if (nullable == null) {
                    val attribute = Attribute(qualifier = qualifier(location))
                    attributeLocations[location] = attribute
                    attribute
                } else nullable
            }
        }

        val qualifier = object : AttributeQualifier {
            override fun toString() = qualifierName
        }

        fun qualifier(location: Int) = object : AttributeQualifier {
            override val qualifierName: String
                get() = "layout(location = $location) ${super.qualifierName}"

            override fun toString(): String = qualifierName
        }

    }
}