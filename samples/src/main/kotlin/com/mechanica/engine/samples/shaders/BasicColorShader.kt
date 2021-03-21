package com.mechanica.engine.samples.shaders

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.Vec2AttributeArray
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript

fun main() {
    Game.configure {
        setFullscreen(false)
    }

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
        val colorInput = uniform.vec4()

        //language=GLSL
        override val main: String = """
            out vec4 colorOutput;
            
            void main(void) {
                colorOutput = $colorInput;
            }
        """

    }

    // Create the shader using the defined shader scripts
    val shader = Shader.create(vertexShader, fragmentShader)

    // Create the array of coordinates that we want to pass to the 'vertexShader.position' attribute
    // It is also possible to use Vector2Arrays.createRectangle() as a shortcut for doing this
    val coordinatesForSquare: Array<Vector2> = arrayOf(
            vec(0.0, 0.0),
            vec(0.0, 0.4),
            vec(0.5, 0.4),
            vec(0.5, 0.0),
    )

    // Use these coordinates to create an attribute array and attach it to vertexShader.position
    val positionArray = Vec2AttributeArray(coordinatesForSquare)
    positionArray.attachTo(vertexShader.position)

    Game.loop {
        fragmentShader.colorInput.set(Mouse.normalized.x, Mouse.normalized.y, 0.5, 1.0)

        // The shader can take the positionArray as an input. The render function can take an array of attributes if many are needed
        shader.render(positionArray) { drawTriangleFan.arrays(4)}
    }
}