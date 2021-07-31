package com.mechanica.engine.shaders.uniforms

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.script.ScriptVariables
import com.mechanica.engine.shaders.vars.ShaderType
import kotlin.reflect.KProperty

class Uniform(private val variables: ScriptVariables) : UniformVars {
    private val uniforms: UniformVars = ShaderFactory.uniformFactory.variables

    override fun <T> type(type: ShaderType<T>, name: String, initialValue: T, load: (T) -> Unit): UniformVar<T> {
        val v = object : UniformVar<T>() {
            override var value: T = initialValue
            override val name: String = name
            override val type = type
            override fun loadUniform() { load(value) }
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                this.value = value
            }
        }
        return addVariable(v)
    }

    override fun <T> type(type: ShaderType<T>, initialValue: T, load: (T) -> Unit): UniformVar<T> {
        val variableName = variables.getNextName()
        return type(type, variableName, initialValue, load)
    }

    override fun float(f: Float, name: String?): UniformFloat {
        val variableName = name ?: variables.getNextName()
        val v = uniforms.float(f, variableName)
        return addVariable(v)
    }

    override fun vec2(x: Number, y: Number, name: String?): UniformVector2 {
        val variableName = name ?: variables.getNextName()
        val v = uniforms.vec2(x, y, variableName)
        return addVariable(v)
    }

    override fun vec3(x: Number, y: Number, z: Number, name: String?): UniformVector3 {
        val variableName = name ?: variables.getNextName()
        val v = uniforms.vec3(x, y, z, variableName)
        return addVariable(v)
    }

    override fun vec4(x: Number, y: Number, z: Number, w: Number, name: String?): UniformVector4 {
        val variableName = name ?: variables.getNextName()
        val v = uniforms.vec4(x, y, z, w, variableName)
        return addVariable(v)
    }

    override fun mat4(name: String?): UniformMatrix4f {
        val variableName = name ?: variables.getNextName()
        val matrix = Matrix4.identity()

        val v = uniforms.mat4(matrix, variableName)
        return addVariable(v)
    }

    private inline fun <reified T : UniformVar<*>> addVariable(v: T): T {
        val new = variables.addVariable(v)
        return try {
            new as T
        } catch (ex: ClassCastException) {
            val message = "A variable in the shader already exists with the name ${v.name} and it has a different type\n" +
                    "The existing variables type is ${new.type} and it can't be cast to ${v.type}"
            throw ClassCastException(message)
        }
    }

}