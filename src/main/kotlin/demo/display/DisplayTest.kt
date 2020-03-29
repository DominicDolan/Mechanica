package demo.display

import display.GLFWContext
import display.Monitor
import display.Window
import gl.renderer.Renderer
import gl.utils.GLContext
import gl.utils.startFrame
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL40
import resources.Res
import util.colors.hex

fun main() {
    val monitor = Monitor.getPrimaryMonitor()
    val window = Window.create("Display Test", 1280, 720)

    window.setIcon(Res.image["red-heart_2764"])
    GLContext.initialize(window)
    GL40.glClearColor(1f, 1f, 0f, 1f)
    val renderer = Renderer()
    val transformation = Matrix4f().identity()
    transformation.scale(0.5f, 0.5f, 1f)
    transformation.rotate(5f, 0f, 0f, 1f)

    while (!window.update()) {
        startFrame()

        renderer.color = hex(0xFF0000FF)
        renderer.render(transformation = transformation)
    }
    GLFWContext.terminate()
}