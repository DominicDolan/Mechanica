package com.mechanica.engine.gl.script

import com.mechanica.engine.gl.glvars.GLVar
import com.mechanica.engine.gl.vbo.pointer.AttributePointer

abstract class ShaderDeclarations(variableName: String = "autoVal") {
    private val variables = ScriptVariables(variableName)
    protected val iterator: Iterator<GLVar<*>>
        get() = variables.iterator()

    val declarations: String
        get() = variables.declarations

    protected open val uniform: Qualifier = qualifier("uniform")
    protected open fun attribute(pointer: AttributePointer) = qualifier("layout (location=${pointer.index}) in")

    fun qualifier(name: String) = object : Qualifier(variables) {
        override val qualifierName: String
            get() = name
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