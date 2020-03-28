package demo.display

import display.Display
import display.Monitor
import display.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwGetWindowMonitor
import org.lwjgl.opengl.GL40
import resources.Res

fun main() {
    // Initialize  Most GLFW functions will not work before doing this.
    check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
    val monitor = Monitor.getPrimaryMonitor()
    val window = Window.create("Display Test", 1280, 720)
    window.isResizable = false

    GL40.glClearColor(1f, 1f, 0f, 0.1f)
    window.setIcon(Res.image["red-heart_2764"])

    while (!GLFW.glfwWindowShouldClose(window.id)) {
        GL40.glClear(GL40.GL_COLOR_BUFFER_BIT)

        GLFW.glfwSwapBuffers(window.id)
        GLFW.glfwPollEvents()
    }
}