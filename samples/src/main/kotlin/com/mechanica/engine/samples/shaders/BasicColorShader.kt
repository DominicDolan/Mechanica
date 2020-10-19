package com.mechanica.engine.samples.shaders

import com.mechanica.engine.color.Color
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript

fun main() {
    Game.configure {  }

    val vertexShader = object : ShaderScript() {
        val position = attribute(0).vec2()

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

    val squareModel = Model.createUnitSquare()

    Game.loop {
        fragmentShader.colorInput.set(Mouse.normalized.x, Mouse.normalized.y, 0.5, 1.0)
        shader.render(squareModel)
    }
}