package com.mechanica.engine.samples.temp

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.geometry.triangulation.delaunay.DelaunayEdge
import com.mechanica.engine.geometry.triangulation.delaunay.DelaunayTriangle
import com.mechanica.engine.geometry.triangulation.delaunay.DelaunayTriangulator
import com.mechanica.engine.geometry.triangulation.delaunay.SuperPoint
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.shaders.models.PolygonModel

fun main() {
    Game.configure {
        setFullscreen(false)
        setStartingScene { TriangulationScene() }
        setViewport(height = 4.0)
    }

    Game.loop()
}

class TriangulationScene : Scene() {
    private var superTriangles: List<DelaunayTriangle> = emptyList()
    private var polygons: List<PolygonModel> = emptyList()
    private var trianglePaths: List<Array<Vector2>> = emptyList()
    private val triangulator: DelaunayTriangulator
    private val points: Array<Vector2> = createRandomPoints(1024)

    init {
        triangulator = DelaunayTriangulator(points)
        triangulate()
    }

    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {

        polygons.forEach {
            draw.green.polygon(it)
        }

        superTriangles.forEach { triangle ->
            triangle.forEachEdge {
                draw.blue.stroke(0.02).delaunayEdge(it)
            }
        }

        trianglePaths.forEach {
            draw.blue.stroke(0.01).path(it)
        }

        points.forEach {
            draw.red.circle(it, 0.02)
        }
    }

    private fun triangulate() {
        triangulator.triangulate()
        trianglePaths = triangulator.triangles
            .filter { !it.hasSuperPoint }
            .map { arrayOf(it.a, it.b, it.c) }

        superTriangles = triangulator.triangles
            .filter { it.hasSuperPoint }

        polygons = trianglePaths.map { PolygonModel(it) }

        trianglePaths = trianglePaths.map {arr -> Array(4) { if (it != 3) arr[it] else arr[0] } }
    }

    private fun Drawer.delaunayEdge(edge: DelaunayEdge) {
        fun drawEdgeWithSuperPoint(point: Vector2, superPoint: Vector2) {
            val y = if (superPoint is SuperPoint.TopPoint) Game.world.top + 1.0 else Game.world.bottom - 1.0
            val x = point.x
            this.line(x, y, x, point.y)
        }

        if (edge.hasSuperPoint) {
            if (edge.p1 is SuperPoint && edge.p2 !is SuperPoint) {
                drawEdgeWithSuperPoint(edge.p2, edge.p1)
            } else if (edge.p2 is SuperPoint && edge.p1 !is SuperPoint) {
                drawEdgeWithSuperPoint(edge.p1, edge.p2)
            }
        } else {
            this.line(edge)
        }
    }

    private fun createRandomPoints(size: Int): Array<Vector2> {
        return Array(size) { Vector2.create(Math.random()*4.0 - 2.0, Math.random()*4.0 - 2.0)}
    }
}