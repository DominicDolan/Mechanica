package com.mechanica.engine.samples.temp

import com.mechanica.engine.color.hex
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.resources.Res
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.text.Text
import com.mechanica.engine.unit.angle.degrees
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.utils.loadImage
import com.mechanica.engine.vertices.IndexArray
import org.intellij.lang.annotations.Language
import org.joml.Matrix4f
import org.lwjgl.opengl.GL40

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setMultisampling(0)
    }

    Attribute.location(0).vec3().createBuffer(emptyArray<Vector>())
    val vertex = object : ShaderScript() {

        val position = attribute(0).vec4()
        val color = attribute(1).vec4()

        val transformation = uniform.mat4()
        val view = uniform.mat4(Game.matrices.worldView)
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
    val indices = IndexArray.create(0, 1, 2, 2, 1, 3)
    val squareArray: Array<Vector> = arrayOf(
            vec(0.25, 0.25),
            vec(0.8, 0.25),
            vec(0.2, 0.85),
            vec(0.85, 0.95)
    )

    val square = Attribute.location(0).vec2().createBuffer(squareArray)

    val colorArray = arrayOf(
            hex(0xFF00FFFF),
            hex(0xFFFF00FF),
            hex(0x0000FFFF),
            hex(0x00FF00FF))
    val colors = vertex.color.createBuffer(colorArray)

    val shader = Shader(vertex, fragment)
    val inputs: Array<Bindable> = arrayOf(square, colors, indices)

    val image = loadImage(Res.image["testImage"])
    val draw = Drawer.create()

    val textModel = TextModel("")
    val text = Text("Hello, text")
    Game.run {
        transformation.identity()
        val mouse = vec(Mouse.world.x/5.0, Mouse.world.y/5.0)
        transformation.scale(5.0f, 5.0f, 1.0f)
        vertex.transformation.set(transformation)
        fragment.mouse.set(mouse)
        shader.render(inputs) {
            GL40.glDrawElements(GL40.GL_TRIANGLES, 6, GL40.GL_UNSIGNED_SHORT, 0)
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

