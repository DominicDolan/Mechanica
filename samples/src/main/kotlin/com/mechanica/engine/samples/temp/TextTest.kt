package com.mechanica.engine.samples.temp

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.models.TextModel
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript
import org.intellij.lang.annotations.Language

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
    }

    val vertex = object : ShaderScript() {

        val position = attribute(Attribute.positionLocation).vec2()
        val texCoords = attribute(Attribute.texCoordsLocation).vec2()

        val projection = uniform.mat4(Game.matrices.projection)
        val view = uniform.mat4(Game.matrices.worldCamera)
        val transformation = uniform.mat4(Game.matrices.worldCamera)

        @Language("GLSL")
        override val main: String = """
            out vec2 tc;
            void main(void) {
                tc = $texCoords;
                gl_Position = $projection*$view*$transformation*vec4($position, 0.0, 1.0);
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

    Game.loop {
        shader.render(model)
    }
}