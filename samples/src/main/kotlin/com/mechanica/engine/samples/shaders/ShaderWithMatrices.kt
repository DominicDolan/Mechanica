package com.mechanica.engine.samples.shaders

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.cave.library.angle.radians
import com.cave.library.vector.arrays.Vector2Arrays
import com.cave.library.vector.plusAssign
import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.DPad
import com.mechanica.engine.input.DirectionalKeys
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.shaders.attributes.Vec2AttributeArray
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript
import org.intellij.lang.annotations.Language

fun main() {

    Game.configure {
        setFullscreen(false)
        setResolution(2000, 1000)
    }

    val vertexShader = object: ShaderScript() {
        val position = attribute(0).vec2()

        val projection = uniform.mat4()
        val view = uniform.mat4()
        val transformation = uniform.mat4()

        @Language("GLSL")
        override val main: String = """
            void main(void) {
                gl_Position = $projection*$view*$transformation*vec4($position, 0.0, 1.0);
            }
        """
    }

    val fragmentShader = object : ShaderScript() {

        @Language("GLSL")
        override val main = """
            out vec4 colorOut;
            
            void main(void) {
                colorOut = vec4(1.0, 0.0, 0.0, 1.0);
            }
        """

    }

    vertexShader.view.matrix.translation.z = -10.0
    vertexShader.projection.matrix.perspective(70.degrees, Game.world.ratio)

    val square = Vector2Arrays.createRectangle(0.0, 0.0, 0.2, 0.2)

    val positionAttribute = Vec2AttributeArray(square)
    positionAttribute.attachTo(vertexShader.position)

    val shader = Shader.create(vertexShader, fragmentShader)

    val dpad = DPad.createWithWASD(0.1)
    val zoom = DirectionalKeys(Mouse.scrollDown, Mouse.scrollUp)
    val rotate = DirectionalKeys(Keyboard.Q, Keyboard.E, 0.1)

    Game.loop {
        vertexShader.transformation.matrix.translation += dpad.value
        vertexShader.transformation.matrix.rotation.angle += rotate.value.radians
        vertexShader.view.matrix.translation.z += zoom.value

        shader.render(positionAttribute) {
            drawTriangles.arrays(6)
        }

    }
}