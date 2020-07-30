package com.mechanica.engine.samples.shaders

import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.Model
import org.joml.Matrix4f

class FragmentRenderer {

    private val vbo = Attribute.location(0).vec3().createUnitQuad()
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