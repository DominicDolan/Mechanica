package com.mechanica.engine.samples.temp

import com.mechanica.engine.game.Game
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.script.Shader
import org.intellij.lang.annotations.Language

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val vertex = object : ShaderScript() {

        val position = attribute(0).vec2()
        val texCoords = attribute(1).vec2()

        val projection = uniform.mat4(Game.matrices.projection)
        val transformation = uniform.mat4(Game.matrices.view)

        @Language("GLSL")
        override val main: String = """
            out vec2 tc;
            void main(void) {
                tc = $texCoords;
                gl_Position = $projection*$transformation*vec4($position, 0.0, 1.0);
            }
        """
    }

    val fragment = object : ShaderScript() {
        @Language("GLSL")
        override val main: String = """
            out vec4 fragColor;
            in vec2 tc;
            layout (binding=0) uniform sampler2D atlas;
            
            void main(void) {
                fragColor = texture(atlas, tc);
            }
        """
    }

    val shader = Shader(vertex, fragment)

    val model = TextModel("Hello\nworld")

    Game.run {
        shader.render(model)
    }
}