package com.mechanica.engine.samples.temp

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.geometry.triangulation.delaunay.DelaunayTriangulator
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.utils.ElementIndexArray

fun main() {
    Game.configure {
        setFullscreen(false)
        setViewport(height = 5.0)
        setStartingScene { DelaunayTriangulationDemo() }
    }

    Game.loop()
}


class DelaunayTriangulationDemo : Scene() {

    private val vertices = createRandomPoints(20)
    private val triangulator = DelaunayTriangulator(vertices)
    private val delaunayModel = Model(
        AttributeArray.createPositionArray(vertices),
        ElementIndexArray(triangulator.triangulate())
    )

    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {
        draw.green.alpha(0.2).model(delaunayModel)
    }

    private fun createRandomPoints(size: Int): Array<Vector2> {
        return Array(size) { Vector2.create(Math.random()*4.0 - 2.0, Math.random()*4.0 - 2.0)}
    }
}