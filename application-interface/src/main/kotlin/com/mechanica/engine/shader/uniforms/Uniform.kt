package com.mechanica.engine.shader.uniforms

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.context.loader.UniformLoader
import com.mechanica.engine.shader.script.ScriptVariables
import com.mechanica.engine.shader.vars.ShaderType
import org.joml.Matrix4f
import kotlin.reflect.KProperty

class Uniform(private val variables: ScriptVariables) : UniformVars {
    private val loader: UniformLoader = MechanicaLoader.shaderLoader.uniformLoader

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
        val v = loader.createUniformFloat(variableName, f)
        return addVariable(v)
    }

    override fun vec2(x: Number, y: Number, name: String?): UniformVector2f {
        val variableName = name ?: variables.getNextName()
        val v = loader.createUniformVec2(variableName, x, y)
        return addVariable(v)
    }

    override fun vec3(x: Number, y: Number, z: Number, name: String?): UniformVector3f {
        val variableName = name ?: variables.getNextName()
        val v = loader.createUniformVec3(variableName, x, y, z)
        return addVariable(v)
    }

    override fun vec4(x: Number, y: Number, z: Number, w: Number, name: String?): UniformVector4f {
        val variableName = name ?: variables.getNextName()
        val v = loader.createUniformVec4(variableName, x, y, z, w)
        return addVariable(v)
    }

    override fun mat4(name: String?): UniformMatrix4f {
        val variableName = name ?: variables.getNextName()
        val matrix = Matrix4f()

        val v = loader.createUniformMat4(variableName, matrix)
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