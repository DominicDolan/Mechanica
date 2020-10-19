package com.mechanica.engine.shader.script

import com.mechanica.engine.shader.uniforms.vars.UniformVar
import com.mechanica.engine.shader.vars.ShaderVariableDefinition

class ScriptVariables(private val placeHolder: String): Iterable<ShaderVariableDefinition> {
    private val variables = ArrayList<ShaderVariableDefinition>()
    private val functions = ArrayList<String>()

    val scriptHead: String
        get() = "#version 400 core\n\n$declarations"

    val declarations: String
        get() {
            val sb = StringBuilder()
            for (v in variables) {
                sb.append(v.declaration)
            }
            for (f in functions) {
                sb.append(f)
            }
            return sb.toString()
        }
    fun getNextName(): String {
        return "$placeHolder${variableIncrement++}"
    }

    fun addVariable(v: ShaderVariableDefinition): ShaderVariableDefinition {
        val existingVariable = variables.byName(v.name)
        return if (existingVariable != null) {
            existingVariable
        } else {
            variables.add(v)
            v
        }
    }

    fun addFunction(f: String) {
        functions.add(f.trimIndent())
    }

    private fun ArrayList<UniformVar<*>>.containsName(name: String): Boolean {
        for (v in this) {
            if (v.name == name) return true
        }
        return false
    }

    private fun ArrayList<ShaderVariableDefinition>.byName(name: String): ShaderVariableDefinition? {
        for (v in this) {
            if (v.name == name) return v
        }
        return null
    }

    override fun iterator(): Iterator<ShaderVariableDefinition> {
        return variables.iterator()
    }

    companion object {
        private var variableIncrement = 0
    }
}