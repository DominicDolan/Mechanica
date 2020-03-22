package demo.polygon

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.utils.createUnitSquareArray
import gl.utils.loadImage
import gl.vbo.AttributeArray
import gl.vbo.VBO
import gl.vbo.pointer.VBOPointer
import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL20
import resources.Res
import state.State
import util.extensions.degrees
import util.extensions.toFloatArray
import util.extensions.toFloatBuffer

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    val renderer = PolygonRenderer()

    private val vbo = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)

    private val model = Model(vbo) /*{
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        glBegin( GL_POLYGON );                //draw solid polygon
        glVertex2i( 0, 0 );
        glVertex2i( 0, 3 );
        glVertex2i( 3, 3 );
        glVertex2f( 3.5f, 1.5f );
        glVertex2f( 2.5f, 0.7f );
        glVertex2i( 3, 0 );
        glEnd();
    }*/

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        renderer.render(model)
    }

}


