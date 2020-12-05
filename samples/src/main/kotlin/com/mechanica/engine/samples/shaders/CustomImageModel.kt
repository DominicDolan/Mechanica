package com.mechanica.engine.samples.shaders

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.Model
import com.mechanica.engine.resources.Res
import com.mechanica.engine.shader.attributes.Attribute
import com.mechanica.engine.shader.attributes.Vec2AttributeArray
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.VectorShapes


fun main() {
    Game.configure {
        setFullscreen(false)
        setViewport(height = 10.0)
    }

    val vertexShader = object : ShaderScript() {
        val position = attribute(Attribute.positionLocation).vec2()
        val textureCoordinates = attribute(Attribute.texCoordsLocation).vec2()

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

    val squareCoordinates: Array<Vector> = VectorShapes.createRectangle(-0.5, -0.5, 0.7, 1.0)
    val positionAttribute = Vec2AttributeArray(squareCoordinates)
    positionAttribute.attachTo(vertexShader.position)

    val textureCoordinates: Array<Vector> = VectorShapes.createRectangle(0.0, 0.0, 1.0, -1.0)
    val textureCoordinatesAttribute = Vec2AttributeArray(textureCoordinates)
    textureCoordinatesAttribute.attachTo(vertexShader.textureCoordinates)

    val image = Image.loadImage(Res.image("testImage"))

    val model = Model(image, positionAttribute, textureCoordinatesAttribute)

    Game.loop {
        shader.render(model)
    }
}