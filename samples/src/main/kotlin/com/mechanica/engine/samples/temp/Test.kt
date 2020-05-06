package com.mechanica.engine.samples.temp

import com.mechanica.engine.color.hex
import com.mechanica.engine.game.Game
import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.gl.qualifiers.Attribute
import com.mechanica.engine.gl.script.ShaderScript
import com.mechanica.engine.gl.shader.Shader
import com.mechanica.engine.gl.utils.createUnitSquareVecArray
import com.mechanica.engine.gl.vbo.ElementArrayBuffer
import com.mechanica.engine.gl.vbo.ElementArrayType
import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.toFloatArray
import org.intellij.lang.annotations.Language
import org.lwjgl.opengl.GL11.glGetIntegerv
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL40

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val vertex = object : ShaderScript() {

        val color = attribute(1).vec4()
        val position = attribute(0).vec2()

        @Language("GLSL")
        override val main: String = """
            out vec4 color;
            void main(void) {
                color = $color;
                gl_Position = vec4($position, 0.0, 1.0);
            }
        """
    }

    val fragment = object : ShaderScript() {

        @Language("GLSL")
        override val main: String = """
            out vec4 fragColor;
            in vec4 color;
            void main(void) {
                fragColor = color;
            }
        """
    }
    val indices = ElementArrayBuffer.create(0, 1, 2, 2, 1, 3)
    val squareArray: Array<Vector> = arrayOf(
            vec(0.25, 0.25),
            vec(0.8, 0.25),
            vec(0.2, 0.85),
            vec(0.85, 0.95)
    )

    val square = vertex.position.createBuffer(squareArray)

    val colorArray = arrayOf(
            hex(0xFF00FFFF),
            hex(0xFFFFFFFF),
            hex(0x0000FFFF),
            hex(0x00FF00FF))
    val colors = vertex.color.createBuffer(colorArray)

    val shader = Shader(vertex, fragment)
    val inputs: Array<Bindable> = arrayOf(square, colors, indices)

    Game.run {
        square.bind()
        shader.render(inputs) {
            GL40.glDrawElements(GL40.GL_TRIANGLES, 6, GL40.GL_UNSIGNED_SHORT, 0)
        }
    }

}

