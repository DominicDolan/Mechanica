package com.mechanica.engine.shader.script

import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.qualifiers.Uniform
import com.mechanica.engine.shader.uniforms.UniformVars
import com.mechanica.engine.shader.vars.ShaderVariableDefinition
import com.mechanica.engine.shader.vars.attributes.AttributeVars

abstract class ShaderDeclarations(variableName: String = "autoVal") {
    private val variables = ScriptVariables(variableName)
    protected val iterator: Iterator<ShaderVariableDefinition>
        get() = variables.iterator()

    val declarations: String
        get() = variables.declarations

    open val uniform: UniformVars = Uniform(variables)
    open fun attribute(location: Int): AttributeVars = Attribute(location, variables)

    fun qualifier(name: String) = object : Qualifier {
        override val qualifierName = name
        override fun toString() = qualifierName
    }

    fun addOther(body: String) {
        variables.addFunction(body)
    }

    companion object {
        var version = "430"
        val header: String
            get() = "#version $version core\n"
    }
}