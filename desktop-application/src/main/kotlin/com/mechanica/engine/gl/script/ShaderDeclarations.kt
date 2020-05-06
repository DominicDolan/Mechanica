package com.mechanica.engine.gl.script

import com.mechanica.engine.gl.qualifiers.Attribute
import com.mechanica.engine.gl.vars.uniforms.UniformVars
import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.qualifiers.Uniform
import com.mechanica.engine.gl.vars.ShaderVariable
import com.mechanica.engine.gl.vars.attributes.ShaderAttributeVars

abstract class ShaderDeclarations(variableName: String = "autoVal") {
    private val variables = ScriptVariables(variableName)
    protected val iterator: Iterator<ShaderVariable>
        get() = variables.iterator()

    val declarations: String
        get() = variables.declarations

    open val uniform: UniformVars = Uniform(variables)
    open fun attribute(location: Int): ShaderAttributeVars = Attribute(location, variables)

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