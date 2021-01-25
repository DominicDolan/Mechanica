package com.mechanica.engine.samples.temp

import com.mechanica.engine.color.hex
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.resources.Res
import com.mechanica.engine.resources.create
import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.models.TextModel
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.engine.shaders.text.Text
import com.mechanica.engine.shaders.utils.ElementIndexArray
import com.mechanica.engine.unit.angle.degrees
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import org.intellij.lang.annotations.Language
import org.joml.Matrix4f

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        setMultisampling(0)
    }

    val vertex = object : ShaderScript() {

        val position = attribute(Attribute.positionLocation).vec4()
        val color = attribute.vec4()

        val transformation = uniform.mat4()
        val view = uniform.mat4(Game.matrices.worldCamera)
        val projection = uniform.mat4(Game.matrices.projection)

        @Language("GLSL")
        override val main: String = """
            out vec4 color;
            out vec2 pos;
            void main(void) {
                pos = $position.xy;
                color = $color;
                gl_Position = $projection*$view*$transformation*vec4($position);
            }
        """
    }

    val fragment = object : ShaderScript() {

        val mouse = uniform.vec2()

        @Language("GLSL")
        override val main: String = """
            out vec4 fragColor;
            in vec2 pos;
            in vec4 color;
            void main(void) {
                if (length(pos - $mouse) < 0.05) {
                    fragColor = vec4(0.0, 1.0, 0.0, 1.0);
                } else {
                    fragColor = color;
                }
            }
        """
    }

    val transformation = Matrix4f().identity()
    val indices = ElementIndexArray(shortArrayOf(0, 1, 2, 2, 1, 3))
    val squareArray: Array<Vector> = arrayOf(
            vec(0.25, 0.25),
            vec(0.8, 0.25),
            vec(0.2, 0.85),
            vec(0.85, 0.95)
    )

    val square = AttributeArray.createPositionArray(squareArray)

    val colorArray = arrayOf(
            hex(0xFF00FFFF),
            hex(0xFFFF00FF),
            hex(0x0000FFFF),
            hex(0x00FF00FF))
    val colors = AttributeArray.create(colorArray, vertex.color)

    val shader = Shader(vertex, fragment)
    val inputs: Array<Bindable> = arrayOf(square, colors, indices)

    val image = Image.create(Res.image["testImage"])
    val draw = Drawer.create()

    val textModel = TextModel("")
    val text = Text("Hello, text")
    Game.loop {
        transformation.identity()
        val mouse = vec(Mouse.world.x/5.0, Mouse.world.y/5.0)
        transformation.scale(5.0f, 5.0f, 1.0f)
        vertex.transformation.set(transformation)
        fragment.mouse.set(mouse)
        shader.render(inputs) {
            this.drawTriangles.elements(indices.vertexCount)
//            GL40.glDrawElements(GL40.GL_TRIANGLES, 6, GL40.GL_UNSIGNED_SHORT, 0)
//            GL40.glDrawArrays(GL40.GL_TRIANGLE_STRIP, 0, 4)
        }
        draw.green.rectangle()
        draw.green.rectangle(1, 1)
        draw.green.rectangle(1, 0)
        draw.green.rectangle(1, 1)
        draw.green.rectangle(2, 0)
        draw.green.rectangle(2, 1)
        draw.rotated(15.degrees).red.rectangle(0, 1, 1, 6)
        draw.blue.circle()
        draw.image(image, -1, 0)
        shader.render(textModel)
        if (Keyboard.enter.hasBeenPressed) {
            textModel.string = "Enter Pressed"
        }
        draw.text(text)
    }

}

