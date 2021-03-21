package com.mechanica.engine.samples.shaders

import com.cave.library.color.Color
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

    // By implementing DrawerScript instead of ShaderScript there are a number of
    // helpful variables and functions that have already been declared
    val vertexShader = object : DrawerScript() {
        //language=GLSL
        override val main: String = """
            void main(void) {
                // position has already been declared as a vec3
                // It will have its values passed in from the Drawer later
                gl_Position = projection*view*transformation*vec4($position, 1.0);
            }
        """
    }

    val fragmentShader = object : DrawerScript() {
        //language=GLSL
        override val main: String = """
            out vec4 colorOutput;
            
            void main(void) {
                // Color is also already declared, it will similarly have its
                // value passed in by the Drawer during render
                colorOutput = $color;
            }
        """

    }

    val shader = DrawerShader.create(vertexShader, fragmentShader)

    val draw = Drawer.create()

    Game.loop {
        val colorFromMouse = Color.rgba(Mouse.normalized.y,0.0, 0.0, 1.0)
        draw.color(colorFromMouse).transformed.scale(8.0, 0.5).translate(-4.0, 3.0).shader(shader)
        draw.red.circle(0.0, 0.0, 1.0)
    }
}