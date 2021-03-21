package com.mechanica.engine.shaders.uniforms

import com.cave.library.color.Color
import com.cave.library.color.InlineColor
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.matrix.mat4.StaticMatrix4
import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec3.VariableVector3
import com.cave.library.vector.vec3.Vector3
import com.cave.library.vector.vec4.VariableVector4
import com.cave.library.vector.vec4.Vector4
import com.mechanica.engine.shaders.qualifiers.UniformQualifier
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType
import com.mechanica.engine.shaders.vars.ShaderVar
import com.mechanica.engine.shaders.vars.ShaderVars
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface UniformVars : ShaderVars<UniformQualifier> {

    fun <T> type(type: ShaderType<T>, name: String, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>
    fun <T> type(type: ShaderType<T>, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>

    fun float(f: Float, name: String? = null): UniformFloat
    override fun float(name: String?): UniformFloat = float(0f, name)

    fun vec2(x: Number, y: Number, name: String? = null): UniformVector2
    override fun vec2(name: String?): UniformVector2 = vec2(0f, 0f, name)
    fun vec2(vector: Vector2, name: String? = null): UniformVector2 = vec2(vector.x, vector.y, name)

    fun vec3(x: Number, y: Number, z: Number, name: String? = null): UniformVector3
    fun vec3(vector: Vector3, name: String? = null): UniformVector3 = vec3(vector.x, vector.y, vector.z, name)
    override fun vec3(name: String?): UniformVector3 = vec3(0f, 0f, 0f, name)

    fun vec4(x: Number, y: Number, z: Number, w: Number, name: String? = null): UniformVector4
    override fun vec4(name: String?): UniformVector4 = vec4(0f, 0f, 0f, 0f, name)
    fun vec4(color: Color, name: String? = null): UniformVector4 = vec4(color.r, color.g, color.b, color.a, name)

    override fun mat4(name: String?): UniformMatrix4f

    fun mat4(matrix: Matrix4, name: String? = null): UniformMatrix4f {
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

abstract class UniformVector2(
        x: Number, y: Number,
        override val name: String
) : UniformVar<VariableVector2>(), VariableVector2 by VariableVector2.create(x.toDouble(), y.toDouble()) {
    override val value: VariableVector2 by lazy { this }
    override val type = ShaderType.vec2()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: VariableVector2) {
        this.value.set(value)
    }
}

abstract class UniformVector3(
        x: Number, y: Number, z: Number,
        override val name: String
) : UniformVar<Vector3>() {
    override var value: VariableVector3 = VariableVector3.create()
    override val type = ShaderType.vec3()

    init { set(x, y, z) }

    fun set(x: Number, y: Number, z: Number) {
        value.x = x.toDouble()
        value.y = y.toDouble()
        value.z = z.toDouble()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Vector3) {
        this.value.set(value)
    }
}

abstract class UniformVector4 (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String
) : UniformVar<Vector4>() {
    override var value: VariableVector4 = VariableVector4.create()
    override val type = ShaderType.vec4()

    init { set(x, y, z, w) }

    fun set(color: Color) {
        value.x = color.r
        value.y = color.g
        value.z = color.b
        value.w = color.a
    }

    fun set(color: InlineColor) {
        value.x = color.r
        value.y = color.g
        value.z = color.b
        value.w = color.a
    }

    fun set(x: Number, y: Number, z: Number, w: Number) {
        value.x = x.toDouble()
        value.y = y.toDouble()
        value.z = z.toDouble()
        value.w = w.toDouble()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Vector4) {
        this.value.set(value)
    }
}

abstract class UniformMatrix4f(
        var matrix: Matrix4,
        override val name: String
) : UniformVar<StaticMatrix4>() {
    override val value: StaticMatrix4
        get() = matrix
    override val type = ShaderType.mat4()

    init {
        set(matrix)
    }

    fun set(matrix: StaticMatrix4) {
        this.matrix.set(matrix)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: StaticMatrix4) {
        set(value)
    }
}