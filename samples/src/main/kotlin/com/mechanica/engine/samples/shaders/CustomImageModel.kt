package com.mechanica.engine.samples.shaders

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.Model
import com.mechanica.engine.resources.Res
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.vertices.AttributeArray


fun main() {
    Game.configure { }

    val vertexShader = object : ShaderScript() {
        val position = attribute(0).vec2()
        val textureCoordinates = attribute(1).vec2()

        //language=GLSL
        override val main: String =
                """
                layout (binding=0) uniform sampler2D image;
                out vec2 tc;
                
                void main(void) {
                    tc = $textureCoordinates;
                    gl_Position = vec4($position, 0.0, 1.0);
                }
                """

    }

    val fragmentShader = object : ShaderScript() {
        //language=GLSL
        override val main: String = """
            // This represents the image that will be bound during the draw call
            layout (binding=0) uniform sampler2D image;
            
            out vec4 colorOutput;
            in vec2 tc;
            
            void main(void) {
                colorOutput = texture(image, tc);
            }
        """

    }

    val shader = Shader.create(vertexShader, fragmentShader)

    fun createRectangleCoordinatesArray(left: Double, right: Double, top: Double, bottom: Double): Array<Vector> {
        return arrayOf(
                vec(left, top),
                vec(left, bottom),
                vec(right, bottom),
                vec(left, top),
                vec(right, bottom),
                vec(right, top)
        )
    }

    val squareCoordinates = createRectangleCoordinatesArray(-0.5, 0.5, 0.5, -0.5)

    val textureCoordinates = createRectangleCoordinatesArray(0.0, 1.0, 0.0, 1.0)

    val positionAttribute = AttributeArray
            .createFrom(vertexShader.position)
            .createArray(squareCoordinates)

    val textureCoordinatesAttribute = AttributeArray
            .createFrom(vertexShader.textureCoordinates)
            .createArray(textureCoordinates)

    val image = Image.loadImage(Res.image("testImage"))

    val model = Model(positionAttribute, textureCoordinatesAttribute, image)

    Game.loop {
        shader.render(model)
    }
}