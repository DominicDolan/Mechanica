package shader.script

import shader.glvars.*

import org.joml.Matrix4f
import util.colors.*
import util.units.Vector

abstract class FirstType(private val shader: ScriptVariables) {
    protected abstract val name: String

    fun vec4(): GLVector4f {
        val variableName = shader.getNextName()
        val firstType = name
        val secondType = "vec4"

        val v = GLVector4f(0f, 0f, 0f, 0f, variableName, firstType, secondType)
        shader.addVariable(v)
        return v
    }

    fun vec4(x: Double, y: Double, z: Double, w: Double): GLVector4f = vec4().apply { set(x, y, z, w) }

    fun vec4(color: Color): GLVector4f = vec4().apply { set(color) }

    fun float(f: Float): GLFloat {
        val variableName = shader.getNextName()
        val firstType = name
        val secondType = "float"

        val v =  GLFloat(f, variableName, firstType, secondType)
        shader.addVariable(v)
        return v
    }

    fun vec3(): GLVector3f {
        val variableName = shader.getNextName()
        val firstType = name
        val secondType = "vec3"

        val v =  GLVector3f(0f, 0f, 0f, variableName, firstType, secondType)
        shader.addVariable(v)
        return v
    }


    fun vec3(x: Double, y: Double, z: Double): GLVector3f = vec3().apply { set(x, y, z) }

    fun vec2(): GLVector2f {
        val variableName = shader.getNextName()
        val firstType = name
        val secondType = "vec2"

        val v =  GLVector2f(0f, 0f, variableName, firstType, secondType)
        shader.addVariable(v)
        return v
    }

    fun vec2(x: Double, y: Double): GLVector2f = vec2().apply { set(x, y) }

    fun vec2(vector: Vector): GLVector2f = vec2().apply { set(vector) }

    fun mat4(): GLMatrix4f {
        val variableName = shader.getNextName()
        val firstType = name
        val secondType = "mat4"
        val matrix = Matrix4f()

        val v =  GLMatrix4f(matrix, variableName, firstType, secondType)
        shader.addVariable(v)
        return v
    }

    fun mat4(matrix: Matrix4f): GLMatrix4f {
        val v = mat4()
        v.set(matrix)
        return v
    }
}