package com.mechanica.engine.samples.temp

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.PolygonModel
import com.mechanica.engine.shader.attributes.Attribute
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import org.intellij.lang.annotations.Language

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }


    val vertex = object : ShaderScript() {

        val position = attribute(Attribute.positionLocation).vec2()

        val projection = uniform.mat4(Game.matrices.projection)
        val transformation = uniform.mat4(Game.matrices.worldCamera)

        @Language("GLSL")
        override val main: String = """
            void main(void) {
                gl_Position = $projection*$transformation*vec4($position, 0.0, 1.0);
            }
        """
    }

    val fragment = object : ShaderScript() {
        @Language("GLSL")
        override val main: String = """
            out vec4 fragColor;
            
            void main(void) {
                fragColor = vec4(1.0, 0.0, 1.0, 1.0);
            }
        """
    }

    val shader = Shader(vertex, fragment)


    val points: Array<Vector> = arrayOf(
            vec(0, 0),
            vec(0.5, 0.1),
            vec(1.0, 0.0),
            vec(0.5, 0.5),
            vec(1.0, 0.7),
            vec(1.0, 1.0),
            vec(0.7, 1.0),
            vec(0.7, 0.65),
            vec(0.0, 1.0),
            vec(0.2, 0.5),
            vec(0.0, 0.0)
    )

    val model = PolygonModel(points)

    Game.loop {
        shader.render(model)
    }
}