package com.mechanica.engine.samples.polygon

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.hex
import com.mechanica.engine.color.toColor
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.Model
import org.joml.Matrix4f

class PolygonRenderer() {

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

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    fragColor = $color;
                }
            """

    }

    private val shader = DrawerShader.create(vertex, fragment)

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    private val transformation = Matrix4f()

    init {
        color = hex(0x00FF0080)
    }

    fun render(model: Model) {
        fragment.mouse.set(Mouse.ui.x, Mouse.ui.y)
        shader.render(model, transformation)
    }

}