package temp

import com.mechanica.engine.display.GLFWContext
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.gl.utils.GLContext
import com.mechanica.engine.unit.vector.vec
import org.joml.Matrix4f

fun main() {
    val vector = vec(5, 3)
    println("Hello, world")
    println(vector)
    val window = Window.create("Title", 1000, 1000)

    GLContext.initialize(window)

    while (window.update()) {

    }

}