package com.mechanica.engine.samples.temp

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.Res
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.utils.loadImage
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.IndexArray
import org.intellij.lang.annotations.Language
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT
import org.lwjgl.opengl.GL32.glDrawElementsBaseVertex

fun main() {
    //-javaagent:E:\Src\Lib\lwjglx-debug-1.0.0.jar
    Game.configure {
        setViewport(height = 10.0)
    }

    val vertex = object : ShaderScript() {

        val position = attribute(0).vec2()
        val texCoords = attribute(1).vec2()

        val transformation = uniform.mat4(Matrix4f().identity())
        val view = uniform.mat4(Game.matrices.worldCamera)
        val projection = uniform.mat4(Game.matrices.projection)

        @Language("GLSL")
        override val main = """
        layout (location=0) in vec3 position;
        out vec2 tc;
        void main(void) {
            tc = $texCoords;
            gl_Position = $projection*$view*${transformation}*vec4(position, 1.0);
        }
    """
    }

    val fragment = object : ShaderScript() {
        @Language("GLSL")
        override val main = """
        out vec4 fragColor;
        in vec2 tc;
        layout (binding=0) uniform sampler2D atlas;
        void main(void) {
            fragColor = texture(atlas, tc);
        }
    """
    }

    val shader = Shader(vertex, fragment)

    val square = arrayOf(*createUnitSquare(), *(createUnitSquare()).map { vec(it.x + 2.0, it.y) }.toTypedArray())
    val invertedSquare = arrayOf(*createInvertedSquare(), *createInvertedSquare())
    val tc = AttributeArray.createFrom(vertex.texCoords).createArray(invertedSquare)
    val pos = AttributeArray.createFrom(vertex.position).createArray(square)
    val indices = IndexArray.createIndicesForQuads(2)

    val image = loadImage(Res.image["testImage"])

    val inputs = arrayOf(pos, tc, image, indices)

    Game.loop {
        shader.render(inputs) {
            glDrawElementsBaseVertex(GL_TRIANGLES, 12, GL_UNSIGNED_SHORT, 0, 0)
        }
    }


}

fun createUnitSquare(): Array<Vector> {
    return arrayOf(
            vec(0, 0),
            vec(0, 1),
            vec(1, 0),
            vec(1, 1)
    )
}

fun createInvertedSquare(): Array<Vector> {
    return arrayOf(
            vec(0, 1),
            vec(0, 0),
            vec(1, 1),
            vec(1, 0)
    )
}
