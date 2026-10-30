@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.samples.nativegl.*
import com.mechanica.engine.shaders.context.UniformFactory
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.uniforms.*
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType
import kotlin.reflect.KProperty

class NativeUniformFactory: UniformFactory {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            location = glGetUniformLocation(shader.id.toUInt(), locationName)
        }
    }

    override val variables: UniformVars = NativeUniformVars()
}

class NativeUniformVars : UniformVars {
    override fun <T> type(type: ShaderType<T>, name: String, initialValue: T, load: (T) -> Unit): UniformVar<T> {
        return object : UniformVar<T>() {
            override var value: T = initialValue
            override fun loadUniform() { load(value) }
            override val name: String = name
            override val type: ShaderType<T> = type
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                this.value = value
            }
        }
    }

    override fun <T> type(type: ShaderType<T>, initialValue: T, load: (T) -> Unit): UniformVar<T> {
        return type(type, "", initialValue, load)
    }

    override fun float(f: Float, name: String?): UniformFloat {
        return NativeFloat(f, name ?: "")
    }

    override fun vec2(x: Number, y: Number, name: String?): UniformVector2 {
        return NativeVector2(x, y, name ?: "")
    }

    override fun vec3(x: Number, y: Number, z: Number, name: String?): UniformVector3 {
        return NativeVector3(x, y, z, name ?: "")
    }

    override fun vec4(x: Number, y: Number, z: Number, w: Number, name: String?): UniformVector4 {
        return NativeVector4(x, y, z, w, name ?: "")
    }

    override fun mat4(name: String?): UniformMatrix4f {
        return NativeMatrix4(Matrix4.identity(), name ?: "")
    }

}