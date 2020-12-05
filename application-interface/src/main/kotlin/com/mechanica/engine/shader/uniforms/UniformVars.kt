package com.mechanica.engine.shader.uniforms

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.shader.qualifiers.UniformQualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.shader.vars.ShaderVar
import com.mechanica.engine.shader.vars.ShaderVars
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface UniformVars : ShaderVars<UniformQualifier> {

    fun <T> type(type: ShaderType<T>, name: String, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>
    fun <T> type(type: ShaderType<T>, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>

    fun float(f: Float, name: String? = null): UniformFloat
    override fun float(name: String?): UniformFloat = float(0f, name)

    fun vec2(x: Number, y: Number, name: String? = null): UniformVector2f
    override fun vec2(name: String?): UniformVector2f = vec2(0f, 0f, name)
    fun vec2(vector: Vector, name: String? = null): UniformVector2f = vec2(vector.x, vector.y, name)

    fun vec3(x: Number, y: Number, z: Number, name: String? = null): UniformVector3f
    fun vec3(vector: Vector3f, name: String? = null): UniformVector3f = vec3(vector.x, vector.y, vector.z, name)
    override fun vec3(name: String?): UniformVector3f = vec3(0f, 0f, 0f, name)

    fun vec4(x: Number, y: Number, z: Number, w: Number, name: String? = null): UniformVector4f
    override fun vec4(name: String?): UniformVector4f = vec4(0f, 0f, 0f, 0f, name)
    fun vec4(color: Color, name: String? = null): UniformVector4f = vec4(color.r, color.g, color.b, color.a, name)

    override fun mat4(name: String?): UniformMatrix4f

    fun mat4(matrix: Matrix4f, name: String? = null): UniformMatrix4f {
        val v = mat4(name)
        v.set(matrix)
        return v
    }
}

abstract class UniformVar<T> : ShaderVar<T, UniformQualifier>, ReadWriteProperty<Any?, T> {
    private val glslLocation: GlslLocation by lazy { GlslLocation.create(this) }

    abstract val value: T
    abstract fun loadUniform()

    override val qualifier = uniformQualifier

    override val location: Int
        get() = glslLocation.location

    override fun toString() = name

    override fun setLocation(shader: Shader) {
        glslLocation.setLocation(shader)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = value

    companion object {
        val uniformQualifier = object : UniformQualifier {
            override fun toString() = qualifierName
        }

    }
}

abstract class UniformFloat(
        override var value: Float,
        override val name: String
) : UniformVar<Float>() {
    override val type = ShaderType.float()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        this.value = value
    }
}

abstract class UniformVector2f(
        x: Number, y: Number,
        override val name: String
) : UniformVar<DynamicVector>(), DynamicVector by DynamicVector.create(x.toDouble(), y.toDouble()) {
    override val value: DynamicVector by lazy { this }
    override val type = ShaderType.vec2()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: DynamicVector) {
        this.value.set(value)
    }
}

abstract class UniformVector3f(
        x: Number, y: Number, z: Number,
        override val name: String
) : UniformVar<Vector3f>() {
    override var value: Vector3f = Vector3f()
    override val type = ShaderType.vec3()

    init { set(x, y, z) }

    fun set(x: Number, y: Number, z: Number) {
        value.x = x.toFloat()
        value.y = y.toFloat()
        value.z = z.toFloat()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Vector3f) {
        this.value.set(value)
    }
}

abstract class UniformVector4f (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String
) : UniformVar<Vector4f>() {
    override var value: Vector4f = Vector4f()
    override val type = ShaderType.vec4()

    init { set(x, y, z, w) }

    fun set(color: Color) {
        value.x = color.r.toFloat()
        value.y = color.g.toFloat()
        value.z = color.b.toFloat()
        value.w = color.a.toFloat()
    }

    fun set(color: InlineColor) {
        value.x = color.r.toFloat()
        value.y = color.g.toFloat()
        value.z = color.b.toFloat()
        value.w = color.a.toFloat()
    }

    fun set(x: Number, y: Number, z: Number, w: Number) {
        value.x = x.toFloat()
        value.y = y.toFloat()
        value.z = z.toFloat()
        value.w = w.toFloat()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Vector4f) {
        this.value.set(value)
    }
}

abstract class UniformMatrix4f(
        var matrix: Matrix4f,
        override val name: String
) : UniformVar<Matrix4f>() {
    override val value: Matrix4f = Matrix4f().identity()
    override val type = ShaderType.mat4()

    init {
        set(matrix)
    }

    fun set(matrix: Matrix4f) {
        this.matrix.set(matrix)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Matrix4f) {
        set(value)
    }
}