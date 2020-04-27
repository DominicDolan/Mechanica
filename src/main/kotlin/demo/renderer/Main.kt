package demo.renderer

import drawer.Drawer
import game.Game
import gl.models.PolygonModel
import gl.utils.loadImage
import graphics.Image
import input.Keyboard
import input.Mouse
import matrices.TransformationMatrix
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.opengl.GL11.GL_STENCIL_TEST
import org.lwjgl.opengl.GL11.glEnable
import resources.Res
import state.State
import util.extensions.degrees
import util.extensions.vec


fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { StartMain() }
    }
    Game.run()
}

private class StartMain : State() {
    private val transformation = TransformationMatrix()

    val image: Image
    var timer = 0.0
    var score = 0
    val polygon: PolygonModel

    init {
        transformation.setScale(1f, 1f,1f)

        image = loadImage(Res.image["testImage"])

        val random = listOf(
                vec(0, 0),
                vec(0, 0.4),
                vec(1, 0.5),
                vec(4, 2),
                vec(3.5, -1),
                vec(3, -1.5),
                vec(1, -1)
        )

        polygon = PolygonModel(random)
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
            Game.view.x -= 3.0 * delta
        }
        if (Keyboard.D.isDown) {
            Game.view.x += 3.0 * delta
        }
        if (Keyboard.W.isDown) {
            Game.view.y += 3.0 * delta
        }
        if (Keyboard.S.isDown) {
            Game.view.y -= 3.0 * delta
        }
    }

    val color = NVGColor.create()

    override fun render(draw: Drawer) {

        val cursorFraction = ((Mouse.view.x / Game.view.width) + 0.5)
        val blend = (cursorFraction*360).degrees

        draw.stroke(0.1).transformed.scale(cursorFraction*3.0).blue.polygon(polygon)
        draw.centered.rotated(blend).about(0, 1).image(image, 0, 0, 1, 1)
        draw.image(image, -0.5, -0.5, 1, 1)
        draw.stroke(0.1).red.circle(0, 3, 1.0)
        draw.stroke(0.1).green.line(vec(4, 3), Mouse.world)
    }
}