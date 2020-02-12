package gl

import display.Game
import gl.script.Declarations
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import util.extensions.vec
import util.units.Vector


val positionAttribute = AttributePointer.create(0, 3)
val textCoordsAttribute = AttributePointer.create(1, 2)

internal fun startGame() {
    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    //language=GLSL
    Declarations.function("""
        vec4 matrices(vec4 position) {
            return projection*view*transformation*position;
        }
    """)
}

internal fun startFrame() {
    GL20.glClear(GL20.GL_COLOR_BUFFER_BIT)
    GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

    enableAlphaBlending()
    Renderer.startFrame()
}

fun loadUnitSquare() = loadQuad(0f, 1f, 1f, 0f)

val defaultDrawProcedure: (VBO) -> Unit = {
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)

}


private fun enableAlphaBlending() {
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
}


private fun loadQuad(left: Float, top: Float, right: Float, bottom: Float): Array<Vector> {
    return arrayOf(
            vec(left, top),
            vec(left, bottom),
            vec(right, bottom),
            vec(left, top),
            vec(right, top),
            vec(right, bottom))

}
