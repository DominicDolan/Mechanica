package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.*
import com.mechanica.engine.shaders.context.UniformLoader
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.uniforms.*
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import kotlin.reflect.KProperty

class LwjglUniformLoader: UniformLoader {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            location = GL20.glGetUniformLocation(shader.id, locationName)
        }
    }

    override val variables: UniformVars = LwjglUniformVars()
}

class LwjglUniformVars : UniformVars {
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
        return LwjglFloat(f, name ?: "")
    }

    override fun vec2(x: Number, y: Number, name: String?): UniformVector2f {
        return LwjglVector2f(x, y, name ?: "")
    }

    override fun vec3(x: Number, y: Number, z: Number, name: String?): UniformVector3f {
        return LwjglVector3f(x, y, z, name ?: "")
    }

    override fun vec4(x: Number, y: Number, z: Number, w: Number, name: String?): UniformVector4f {
        return LwjglVector4f(x, y, z, w, name ?: "")
    }

    override fun mat4(name: String?): UniformMatrix4f {
        return LwjglMatrix4f(Matrix4f(), name ?: "")
    }

}