package com.mechanica.engine.shader.qualifiers

import com.mechanica.engine.color.Color
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.shader.script.ScriptVariables
import com.mechanica.engine.shader.vars.ShaderVariable
import com.mechanica.engine.shader.vars.attributes.AttributeVariable
import com.mechanica.engine.shader.vars.attributes.AttributeVars
import com.mechanica.engine.shader.vars.attributes.vars.AttributeType
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.FloatBufferMaker
import org.joml.Vector3f
import org.joml.Vector4f

class Attribute(
        override val location: Int,
        private val variables: ScriptVariables? = null) : AttributeQualifier, AttributeVars {

    private val loader = GLLoader.createAttributeLoader(this)

    override fun float(name: String?): FloatBufferMaker {
        return createFloatAttribute("float", name, 1)
    }

    override fun vec2(name: String?): FloatBufferMaker {
        return createFloatAttribute("vec2", name, 2)
    }

    override fun vec3(name: String?): FloatBufferMaker {
        return createFloatAttribute("vec3", name, 3)
    }

    override fun vec4(name: String?): FloatBufferMaker {
        return createFloatAttribute("vec4", name, 4)
    }

    override fun mat4(name: String?): FloatBufferMaker {
        return createFloatAttribute("mat4", name, 16)
    }

    override fun toString() = qualifierName

    private fun createFloatAttribute(typeName: String, name: String?, coordinateSize: Int): FloatBufferMaker {
        val variableName = name ?: variables?.getNextName() ?: ""
        val type = AttributeType.get(coordinateSize, typeName)
        val variable = VariableAndBufferMaker(variableName, this, type)

        return addVariable(variable)
    }

    private inline fun <reified T : ShaderVariable> addVariable(v: T): T {
        val new = variables?.addVariable(v) ?: v
        return try {
            new as T
        } catch (ex: ClassCastException) {
            val message = "A variable in the shader already exists with the name ${v.name} and it has a different type\n" +
                    "The existing variables type is ${new.type} and it can't be cast to ${v.type}"
            throw ClassCastException(message)
        }
    }

    private inner class VariableAndBufferMaker(
            override val name: String,
            override val qualifier: AttributeQualifier,
            override val type: AttributeType) : AttributeVariable, FloatBufferMaker {

        override fun createBuffer(array: FloatArray) = createBuffer(array.size) { set(array) }
        override fun createBuffer(array: Array<out Vector>) = createBuffer(array.size) { set(array) }
        override fun createBuffer(array: Array<Vector3f>) = createBuffer(array.size) { set(array) }
        override fun createBuffer(array: Array<Vector4f>) = createBuffer(array.size) { set(array) }
        override fun createBuffer(array: Array<out Color>) = createBuffer(array.size) { set(array) }

        private inline fun createBuffer(size: Int, setter: AttributeArray.() -> Unit): AttributeArray {
            val v = loader.createAttributeArray(size, this)
            setter(v)
            return v
        }

        override fun toString() = name
    }

    companion object {
        operator fun invoke(location: Int): AttributeVars {
            return Attribute(location)
        }
    }
}