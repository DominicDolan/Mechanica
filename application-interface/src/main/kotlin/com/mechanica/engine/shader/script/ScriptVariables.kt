package com.mechanica.engine.shader.script

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.shader.uniforms.UniformVar
import com.mechanica.engine.shader.vars.ShaderVar

interface PlatformScriptValues {
    val header: String
    val placeHolder: String
        get() = "autoVal"
}

class ScriptVariables: PlatformScriptValues by MechanicaLoader.shaderLoader.platformScriptValues, Iterable<ShaderVar<*, *>> {
    private val variables = ArrayList<ShaderVar<*, *>>()
    private val functions = ArrayList<String>()

    val scriptHead: String
        get() = "$header\n\n$declarations"

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

    fun addVariable(v: ShaderVar<*, *>): ShaderVar<*, *> {
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

    private fun ArrayList<ShaderVar<*, *>>.byName(name: String): ShaderVar<*, *>? {
        for (v in this) {
            if (v.name == name) return v
        }
        return null
    }

    override fun iterator(): Iterator<ShaderVar<*, *>> {
        return variables.iterator()
    }

    companion object {
        private var variableIncrement = 0
    }
}