package com.mechanica.engine.shaders.vars

import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec3.Vector3
import com.cave.library.vector.vec4.Vector4
import com.mechanica.engine.shaders.attributes.AttributeVar
import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.qualifiers.Qualifier
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.uniforms.UniformVar

interface ShaderVars<Q : Qualifier> {

    fun float(name: String? = null): ShaderVar<Float, Q>

    fun vec2(name: String? = null): ShaderVar<MutableVector2, Q>

    fun vec3(name: String? = null): ShaderVar<Vector3, Q>

    fun vec4(name: String? = null): ShaderVar<Vector4, Q>

    fun mat4(name: String? = null): ShaderVar<Matrix4, Q>


}

interface GlslLocation {
    val location: Int
    fun setLocation(shader: Shader)

    companion object {

        fun create(variable: AttributeVar<*>): GlslLocation {
            return ShaderFactory.attributeFactory.createLocationLoader(variable.locationName)
        }

        fun create(location: Int): GlslLocation {
            return object : GlslLocation {
                override val location: Int = location
                override fun setLocation(shader: Shader) { }
            }
        }

        fun create(variable: UniformVar<*>): GlslLocation {
            return ShaderFactory.uniformFactory.createLocationLoader(variable.locationName)
        }
    }
}

interface ShaderVar<T, Q : Qualifier> : GlslLocation {
    val name: String
    val qualifier: Q
    val type: ShaderType<T>

    val declaration: String
        get() = "$qualifier $type $name; \n"

    val locationName: String
        get() = getLocationName(name)

    override fun toString(): String

    companion object {
        private val regex = Regex("[^\\w\\d]")

        fun getLocationName(name: String): String {
            val index = regex.find(name)?.range?.first
            return if (index != null) {
                name.substring(0 until index)
            } else name
        }
    }
}