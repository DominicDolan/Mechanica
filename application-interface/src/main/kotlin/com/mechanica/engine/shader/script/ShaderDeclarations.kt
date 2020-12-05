package com.mechanica.engine.shader.script

import com.mechanica.engine.shader.attributes.Attribute
import com.mechanica.engine.shader.attributes.AttributeVars
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.uniforms.Uniform
import com.mechanica.engine.shader.uniforms.UniformVars
import com.mechanica.engine.shader.vars.ShaderVar

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