@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.samples

import com.mechanica.nativebackend.NativeDrawerFactory
import com.mechanica.nativebackend.NativeShaderCreator
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

/** Minimal shader used to exercise the engine's Native shader and draw interfaces. */
class Triangle {
    private val program = NativeShaderCreator.fromSource(
        """
            #version 330 core
            void main() {
                vec2 positions[3] = vec2[3](vec2(-0.65, -0.6), vec2(0.65, -0.6), vec2(0.0, 0.65));
                gl_Position = vec4(positions[gl_VertexID], 0.0, 1.0);
            }
        """.trimIndent(),
        """
            #version 330 core
            out vec4 color;
            void main() { color = vec4(0.98, 0.63, 0.22, 1.0); }
        """.trimIndent(),
    )
    private val vertexArray: UInt = memScoped {
        val vao = alloc<UIntVar>()
        glGenVertexArrays(1, vao.ptr)
        vao.value
    }
    private val drawer = NativeDrawerFactory(GL_TRIANGLES.toUInt(), GL_UNSIGNED_INT.toUInt())

    fun draw() {
        program.useShader()
        glBindVertexArray(vertexArray)
        drawer.drawArrays(3)
    }

    fun close() = memScoped {
        val vao = alloc<UIntVar>()
        vao.value = vertexArray
        glDeleteVertexArrays(1, vao.ptr)
        program.close()
    }
}
