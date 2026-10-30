@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

/** A linked OpenGL program owned by the current context. */
class NativeShaderCreator private constructor(
    vertexSource: String,
    fragmentSource: String,
    geometrySource: String?,
) : ShaderCreator {
    override val id: Int

    constructor(vertex: ShaderScript, fragment: ShaderScript, tessellation: ShaderScript?, geometry: ShaderScript?) :
        this(vertexSource(vertex, tessellation), fragment.script, geometry?.script)

    init {
        val shaders = mutableListOf<UInt>()
        var program = 0u
        try {
            shaders += compile(vertexSource, GL_VERTEX_SHADER.toUInt())
            shaders += compile(fragmentSource, GL_FRAGMENT_SHADER.toUInt())
            if (geometrySource != null) shaders += compile(geometrySource, GL_GEOMETRY_SHADER.toUInt())
            program = glCreateProgram()
            check(program != 0u) { "OpenGL failed to create a program" }
            shaders.forEach { glAttachShader(program, it) }
            glLinkProgram(program)
            memScoped {
                val status = alloc<IntVar>()
                glGetProgramiv(program, GL_LINK_STATUS.toUInt(), status.ptr)
                check(status.value == GL_TRUE) { "Shader link failed: ${programLog(program)}" }
            }
            id = program.toInt()
        } catch (error: Throwable) {
            if (program != 0u) glDeleteProgram(program)
            throw error
        } finally {
            shaders.forEach { glDeleteShader(it) }
        }
    }

    override fun useShader() = glUseProgram(id.toUInt())
    override fun loadUniformLocation(name: String): Int = glGetUniformLocation(id.toUInt(), name)
    override fun loadAttributeLocation(name: String): Int = glGetAttribLocation(id.toUInt(), name)
    fun close() = glDeleteProgram(id.toUInt())

    companion object {
        /** Useful for testing a program without initializing the engine's shader factory. */
        fun fromSource(vertex: String, fragment: String): NativeShaderCreator =
            NativeShaderCreator(vertex, fragment, null)
    }
}

private fun vertexSource(vertex: ShaderScript, tessellation: ShaderScript?): String {
    require(tessellation == null) { "Tessellation shaders are not yet supported" }
    return vertex.script
}

private fun compile(source: String, type: UInt): UInt = memScoped {
    val shader = glCreateShader(type)
    check(shader != 0u) { "OpenGL failed to create a shader" }
    try {
        val sources = allocArray<CPointerVar<ByteVar>>(1)
        sources[0] = source.cstr.ptr
        glShaderSource(shader, 1, sources, null)
        glCompileShader(shader)
        val status = alloc<IntVar>()
        glGetShaderiv(shader, GL_COMPILE_STATUS.toUInt(), status.ptr)
        check(status.value == GL_TRUE) { "Shader compilation failed: ${shaderLog(shader)}" }
        shader
    } catch (error: Throwable) {
        glDeleteShader(shader)
        throw error
    }
}

private fun shaderLog(shader: UInt): String = memScoped {
    val length = alloc<IntVar>()
    glGetShaderiv(shader, GL_INFO_LOG_LENGTH.toUInt(), length.ptr)
    val text = allocArray<ByteVar>(length.value.coerceAtLeast(1))
    glGetShaderInfoLog(shader, length.value.coerceAtLeast(1), null, text)
    text.toKString()
}

private fun programLog(program: UInt): String = memScoped {
    val length = alloc<IntVar>()
    glGetProgramiv(program, GL_INFO_LOG_LENGTH.toUInt(), length.ptr)
    val text = allocArray<ByteVar>(length.value.coerceAtLeast(1))
    glGetProgramInfoLog(program, length.value.coerceAtLeast(1), null, text)
    text.toKString()
}
