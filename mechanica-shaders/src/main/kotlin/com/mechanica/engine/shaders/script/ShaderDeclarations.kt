package com.mechanica.engine.shaders.script

import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.AttributeVars
import com.mechanica.engine.shaders.qualifiers.Qualifier
import com.mechanica.engine.shaders.uniforms.Uniform
import com.mechanica.engine.shaders.uniforms.UniformVars
import com.mechanica.engine.shaders.vars.ShaderVar

abstract class ShaderDeclarations() {
    private val variables = ScriptVariables()
    protected val iterator: Iterator<ShaderVar<*, *>>
        get() = variables.iterator()

    val header: String
        get() = variables.header
    val declarations: String
        get() = variables.declarations

    open val uniform: UniformVars = Uniform(variables)
    open val attribute: AttributeVars = Attribute(variables)
    open fun attribute(location: Int) = Attribute(variables, Attribute.qualifier(location))

    fun qualifier(name: String) = object : Qualifier {
        override val qualifierName = name
        override fun toString() = qualifierName
    }

    fun addOther(body: String) {
        variables.addFunction(body)
    }

}