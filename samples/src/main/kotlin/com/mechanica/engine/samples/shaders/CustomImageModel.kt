package com.mechanica.engine.samples.shaders

import com.cave.library.vector.arrays.Vector2Arrays
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.Vec2AttributeArray
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript


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
Vector2Arrays
    val squareCoordinates: Array<Vector2> = Vector2Arrays.createRectangle(-0.5, -0.5, 0.7, 1.0)
    val positionAttribute = Vec2AttributeArray(squareCoordinates)
    positionAttribute.attachTo(vertexShader.position)

    val textureCoordinates: Array<Vector2> = Vector2Arrays.createRectangle(0.0, 0.0, 1.0, -1.0)
    val textureCoordinatesAttribute = Vec2AttributeArray(textureCoordinates)
    textureCoordinatesAttribute.attachTo(vertexShader.textureCoordinates)

    val image = Image.create(Res.image("testImage"))

    val model = Model(image, positionAttribute, textureCoordinatesAttribute)

    Game.loop {
        shader.render(model)
    }
}