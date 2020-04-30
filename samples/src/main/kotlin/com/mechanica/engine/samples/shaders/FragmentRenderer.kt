package com.mechanica.engine.samples.shaders

import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.utils.createUnitSquareVecArray
import com.mechanica.engine.gl.vbo.AttributeArray
import com.mechanica.engine.gl.vbo.pointer.VBOPointer
import com.mechanica.engine.input.Mouse
import com.mechanica.engine.util.extensions.toFloatArray
import org.joml.Matrix4f

class FragmentRenderer {

    private val vbo = AttributeArray(createUnitSquareVecArray().toFloatArray(3), VBOPointer.position)
    private val model = Model(vbo)

    private val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : DrawerScript() {

        //        val unitTests.color = uniform.vec4(hex(0xFF00FFFF))
        val mouse = uniform.vec2(0.0, 0.0)
        val time = uniform.float(0f)
        val resolution = uniform.vec2(Game.window.width.toDouble(), Game.window.height.toDouble())

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    vec2 st = (gl_FragCoord.xy/$resolution - vec2(0.5, 0.5) - $mouse);
                    fragColor = vec4(abs(st.x), abs(st.y), abs(sin($time)), 1.0);
                }
            """

    }

    private val shader = DrawerShader(vertex, fragment)
    private val startTime = System.currentTimeMillis()

    private val transformation = Matrix4f().also { it.identity() }


    fun render() {
        fragment.mouse.set(Mouse.ui.x, Mouse.ui.y)
        fragment.time.value = (System.currentTimeMillis() - startTime).toFloat()/1000f
        shader.render(model, transformation)
    }
}