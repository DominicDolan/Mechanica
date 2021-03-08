package com.mechanica.engine.samples.shaders

import com.cave.library.color.Color
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.Vec2AttributeArray
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript


fun main() {
    Game.configure { }
    val vertexShader = object : ShaderScript() {
        val position = attribute(Attribute.positionLocation).vec2()

        //language=GLSL
        override val main: String = """
            void main(void) {
                gl_Position = vec4($position, 0.0, 1.0);
            }
        """

    }

    val fragmentShader = object : ShaderScript() {
        val colorInput = uniform.vec4(Color.red)

        //language=GLSL
        override val main: String = """
            out vec4 colorOutput;
            
            void main(void) {
                colorOutput = $colorInput;
            }
        """

    }

    val shader = Shader.create(vertexShader, fragmentShader)


    val left = -0.5f
    val right = 0.5f
    val bottom = -0.5f
    val top = 0.5f

    val shapeCoordinates = arrayOf(
            vec(left, top),
            vec(left, bottom),
            vec(right, bottom),
            vec(left, top),
            vec(right, bottom),
            vec(right, top)
    )

    val positionAttribute = Vec2AttributeArray(shapeCoordinates)
    positionAttribute.attachTo(vertexShader.position)

    val model = Model(positionAttribute)

    Game.loop {
        shader.render(model)
    }
}