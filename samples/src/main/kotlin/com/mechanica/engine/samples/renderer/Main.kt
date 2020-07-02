package com.mechanica.engine.samples.renderer

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.PolygonModel
import com.mechanica.engine.resources.Res
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.unit.angle.degrees
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.utils.loadImage
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.GL_STENCIL_TEST
import org.lwjgl.opengl.GL11.glEnable


fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { StartMain() }
    }
    Game.run()
}

private class StartMain : WorldScene() {
    private val transformation = Matrix4f()

    val image: Image
    var timer = 0.0
    var score = 0
    val polygon: PolygonModel

    init {
        transformation.scale(1f, 1f,1f)
        println(transformation)
        println()

        transformation.rotationZ((-Math.PI/6f).toFloat())
        transformation.m01(transformation.m10() + transformation.m01())

//        transformation.rotationZ((-Math.PI/6f).toFloat())
//        transformation.m10(transformation.m10() + transformation.m01())


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
        if (Mouse.MB1.hasBeenPressed) {
            score++
        }
        if (Mouse.MB2.hasBeenPressed) {
            score--
        }
        if (Keyboard.A()) {
            Game.view.x -= 3.0 * delta
        }
        if (Keyboard.D()) {
            Game.view.x += 3.0 * delta
        }
        if (Keyboard.W()) {
            Game.view.y += 3.0 * delta
        }
        if (Keyboard.S()) {
            Game.view.y -= 3.0 * delta
        }
    }

    override fun render(draw: Drawer) {

        val cursorFraction = ((Mouse.ui.x / Game.view.width) + 0.5)
        val blend = (cursorFraction*360).degrees

        draw.stroke(0.1).transformed.scale(cursorFraction*3.0).blue.polygon(polygon)
        draw.centered.rotated(blend).about(0, 1).image(image, 0, 0, 1, 1)
        draw.transformed(transformation).image(image, -0.5, -0.5, 1, 1)
        draw.rotated(30.degrees).image(image, 0.5, -0.5, 1, 1)
        draw.stroke(0.1).red.circle(0, 3, 1.0)
        draw.stroke(0.1).green.line(vec(4, 3), Mouse.world)
    }
}