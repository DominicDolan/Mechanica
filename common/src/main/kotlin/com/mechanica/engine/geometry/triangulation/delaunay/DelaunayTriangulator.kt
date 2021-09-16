package com.mechanica.engine.geometry.triangulation.delaunay

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.util.extensions.add
import com.mechanica.engine.util.extensions.remove

class DelaunayTriangulator(points: Array<out Vector2>) : Triangulator {
    val triangles = ArrayList<DelaunayTriangle>()
    private val points = points.toMutableList()
    override val indices: ShortArray
        get() = TODO("Not yet implemented")
    override val indexCount: Int
        get() = TODO("Not yet implemented")

    init {
        if (points.size < 3) {
            throw IllegalArgumentException("Less than three points in point set.")
        }

        points.shuffle()
    }

    override fun triangulate(): ShortArray {
        triangles.clear()
        val p0 = points.lexicographicMax()
        val superTriangle = DelaunayTriangle(p0, SuperPoint.BottomPoint, SuperPoint.TopPoint)
        triangles.add(superTriangle)

        for (point in points.filter { it != p0 }) {
            val triangle = triangles.find { it.contains(point) }
            if (triangle != null) {
                val (a, b, c) = triangle
                triangles.remove(triangle)

                val first = DelaunayTriangle(a, b, point)
                val second = DelaunayTriangle(b, c, point)
                val third = DelaunayTriangle(c, a, point)

                triangles.add(first, second, third)

                checkAndLegalize(first.ab, second.ab, third.ab)
            } else {
                println("null at point: $point") // TODO: Case when a point is colinear
            }

        }

        return ShortArray(10)
    }

    private fun checkAndLegalize(edge: DelaunayEdge) {
        val coincident = edge.findCoincidentEdge() ?: return

        if (!isLegal(edge, coincident.opposite)) {
            legalizeEdge(edge, coincident)
        }
    }

    private fun checkAndLegalize(edge1: DelaunayEdge, edge2: DelaunayEdge) {
        checkAndLegalize(edge1)
        checkAndLegalize(edge2)
    }

    private fun checkAndLegalize(edge1: DelaunayEdge, edge2: DelaunayEdge, edge3: DelaunayEdge) {
        checkAndLegalize(edge1)
        checkAndLegalize(edge2)
        checkAndLegalize(edge3)
    }

    private fun legalizeEdge(edge: DelaunayEdge, coincident: DelaunayEdge) {
        require(triangles.remove(edge.triangle, coincident.triangle))

        val first = DelaunayTriangle(coincident.opposite, edge.p1, edge.opposite)
        val second = DelaunayTriangle(coincident.opposite, edge.p2, edge.opposite)

        triangles.add(first, second)

        checkAndLegalize(first.ab, second.ab)
    }

    private fun isLegal(edge: DelaunayEdge, coincidentOpposite: Vector2): Boolean {
        if (!edge.hasSuperPoint && coincidentOpposite !is SuperPoint) {
            return !edge.triangle.circumcircleContains(coincidentOpposite)
        } else {
            if (coincidentOpposite !is SuperPoint) {
                val nonSuperPoint = (if (edge.p1 is SuperPoint) edge.p2 else edge.p1)
                val superPoint = (if (edge.p1 is SuperPoint) edge.p1 else edge.p2) as SuperPoint
                return superPoint.isOnRightOf(coincidentOpposite, nonSuperPoint) != edge.opposite.isOnRightOf(coincidentOpposite, nonSuperPoint)
            }
            return true
        }
    }

    private fun DelaunayEdge.findCoincidentEdge(): DelaunayEdge? {
        for (triangle in triangles) {
            if (triangle !== this.triangle && triangle.anyEdge { it == this }) {
                return triangle.findOneEdge { it.hasBothPoints(this.p1, this.p2) }
            }
        }
        return null
    }

    private fun MutableList<Vector2>.lexicographicMax(): Vector2 {
        var lexicographicMax = this[0]
        for (vec in this) {
            if (vec.isLexicographicallyGreater(lexicographicMax))
                lexicographicMax = vec
        }
        return lexicographicMax
    }
}

