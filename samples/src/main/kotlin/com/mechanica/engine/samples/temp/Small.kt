package com.mechanica.engine.samples.temp

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.utils.loadImage
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.resources.Res
import com.mechanica.engine.text.Text
import org.intellij.lang.annotations.Language

fun main() {
    //-javaagent:E:\Src\Lib\lwjglx-debug-1.0.0.jar
    Game.configure {
        setViewport(height = 10.0)
    }

    val vertex = object : ShaderScript() {

        val position = attribute(0).vec2()
        val texCoords = attribute(1).vec2()

        @Language("GLSL")
        override val main = """
            layout (location=0) in vec3 position;
            out vec2 tc;
            void main(void) {
                tc = $texCoords;
                gl_Position = vec4(position*0.3, 1.0);
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

    val tc = vertex.texCoords.createInvertedUnitQuad()
    val pos = vertex.position.createUnitQuad()

    val image = loadImage(Res.image["testImage"])

    val inputs = arrayOf(pos, tc, image)

    val model = TextModel("21")

    val text = Text("Hello\nworld")
    println(text.lineCount)
    val draw = Drawer.create()
    Game.run {
        shader.render(model)
        draw.green.text(text)
    }
}
