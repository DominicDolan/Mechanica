package shader.script

import shader.glvars.*

import org.joml.Matrix4f
import util.colors.*
import util.units.Vector

abstract class Qualifier(private val variables: ScriptVariables) {
    protected abstract val name: String

    fun <T> type(type: String, name: String, initialValue: T): GLVar<T> {
        val qualifier = this.name

        val v = object : GLVar<T>() {
            override var value: T = initialValue
            override val name: String = name
            override val qualifier: String = qualifier
            override val type: String = type
        }
        variables.addVariable(v)
        return v
    }

    fun <T> type(type: String, initialValue: T): GLVar<T> {
        val variableName = variables.getNextName()
        return type(type, variableName, initialValue)
    }

    fun vec4(): GLVector4f {
        val variableName = variables.getNextName()
        val qualifier = name

        val v = GLVector4f(0f, 0f, 0f, 0f, variableName, qualifier)
        variables.addVariable(v)
        return v
    }

    fun vec4(x: Double, y: Double, z: Double, w: Double): GLVector4f = vec4().apply { set(x, y, z, w) }

    fun vec4(color: Color): GLVector4f = vec4().apply { set(color) }

    fun float(f: Float): GLFloat {
        val variableName = variables.getNextName()
        val qualifier = name

        val v =  GLFloat(f, variableName, qualifier)
        variables.addVariable(v)
        return v
    }

    fun vec3(): GLVector3f {
        val variableName = variables.getNextName()
        val qualifier = name

        val v =  GLVector3f(0f, 0f, 0f, variableName, qualifier)
        variables.addVariable(v)
        return v
    }


    fun vec3(x: Double, y: Double, z: Double): GLVector3f = vec3().apply { set(x, y, z) }

    fun vec2(): GLVector2f {
        val variableName = variables.getNextName()
        val qualifier = name

        val v =  GLVector2f(0f, 0f, variableName, qualifier)
        variables.addVariable(v)
        return v
    }

    fun vec2(x: Double, y: Double): GLVector2f = vec2().apply { set(x, y) }

    fun vec2(vector: Vector): GLVector2f = vec2().apply { set(vector) }

    fun mat4(): GLMatrix4f {
        val variableName = variables.getNextName()
        val qualifier = name
        val matrix = Matrix4f()

        val v =  GLMatrix4f(matrix, variableName, qualifier)
        variables.addVariable(v)
        return v
    }

    fun mat4(matrix: Matrix4f): GLMatrix4f {
        val v = mat4()
        v.set(matrix)
        return v
    }
}