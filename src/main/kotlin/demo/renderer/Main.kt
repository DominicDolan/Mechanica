package demo.renderer

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.script.ShaderScript
import gl.utils.IndexedVertices
import gl.utils.loadImage
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import graphics.Image
import graphics.Polygon
import input.Cursor
import input.Keyboard
import matrices.TransformationMatrix
import models.Model
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL2.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import resources.Res
import state.State
import util.colors.hex
import util.colors.rgba
import util.extensions.degrees
import util.extensions.vec
import util.units.Vector
import java.nio.ByteBuffer


fun main() {
    val options = GameOptions()
            .setResolution(1920, 1080)
//            .setFullscreen(true, true)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    private val transformation = TransformationMatrix()

    val image: Image
    var timer = 0.0
    var score = 0
    val polygon: Polygon

    init {
        transformation.setScale(1.0, 1.0,1.0)

        image = loadImage(Res.image["colors"])

        val random = listOf(
                vec(0, 0),
                vec(0, 0.4),
                vec(1, 0.5),
                vec(4, 2),
                vec(3.5, -1),
                vec(3, -1.5),
                vec(1, -1)
        )

        polygon = Polygon.create(random)
        glEnable(GL_STENCIL_TEST)
    }

    override fun update(delta: Double) {
        timer += delta
        if (Keyboard.MB1.hasBeenPressed) {
            score++
        }
        if (Keyboard.MB2.hasBeenPressed) {
            score--
        }
        if (Keyboard.A.isDown) {
            Game.viewX -= 3.0 * delta
        }
        if (Keyboard.D.isDown) {
            Game.viewX += 3.0 * delta
        }
        if (Keyboard.W.isDown) {
            Game.viewY += 3.0 * delta
        }
        if (Keyboard.S.isDown) {
            Game.viewY -= 3.0 * delta
        }
    }

    val color = NVGColor.create()

    override fun render(draw: Drawer) {

        val cursorFraction = ((Cursor.viewX / Game.viewWidth) + 0.5)
        val blend = (cursorFraction*360).degrees

        draw.stroke(0.1).blue.polygon(polygon, scaleWidth = cursorFraction*3.0)
        draw.centered.rotated(blend).about(0, 1).image(image, 0, 0, 1, 1)
        draw.normal.image(image, -0.5, -0.5, 1, 1)
        draw.stroke(0.1).red.circle(0, 3, 1.0)
        draw.stroke(0.1).green.line(vec(4, 3), vec(Cursor.worldX, Cursor.worldY))
    }

    private data class ImageDetails(val data: ByteBuffer?, val id: Int, val width: Int, val height: Int, val components: Int)

}