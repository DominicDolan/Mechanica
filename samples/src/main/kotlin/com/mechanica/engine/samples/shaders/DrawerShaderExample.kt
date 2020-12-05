package com.mechanica.engine.samples.shaders

import com.mechanica.engine.color.Color
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
    }

    val vertexShader = object : DrawerScript() {
        //language=GLSL
        override val main: String = """
            void main(void) {
                gl_Position = projection*view*transformation*vec4($position, 1.0);
                //projection*view*transformation*
            }
        """
    }

    val fragmentShader = object : DrawerScript() {
        //language=GLSL
        override val main: String = """
            out vec4 colorOutput;
            
            void main(void) {
                colorOutput = $color;
            }
        """

    }

    val shader = DrawerShader.create(vertexShader, fragmentShader)

    val draw = Drawer.create()

    Game.loop {
        val colorFromMouse = Color.rgba(Mouse.normalized.x, Mouse.normalized.y, 0.5, 1.0)
        draw.color(colorFromMouse).transformed.scale(8.0, 0.5).translate(-4.0, 3.0).shader(shader)
    }
}