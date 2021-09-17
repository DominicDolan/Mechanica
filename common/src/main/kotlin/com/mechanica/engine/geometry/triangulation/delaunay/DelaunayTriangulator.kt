package com.mechanica.engine.geometry.triangulation.delaunay

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.triangulation.Triangulator

class DelaunayTriangulator(points: Array<out Vector2>) : Triangulator {
    val triangles = ArrayList<DelaunayTriangle>()
    private val points = points.toMutableList()
    override val indexCount: Int = 0

    init {
        if (points.size < 3) {
            throw IllegalArgumentException("Less than three points in point set.")
        }
    }

    override fun triangulate(): ShortArray {
        triangles.clear()
        val p0 = points.lexicographicMax()
        val superTriangle = DelaunayTriangle(p0, SuperPoint.BottomPoint, SuperPoint.TopPoint)
        triangles.add(superTriangle)

        for (point in points.filter { it != p0 }.shuffled()) {
            val triangle = triangles.find { it.contains(point) == 1 }
            if (triangle != null) {
                interiorCase(point, triangle)
            } else {
                collinearCase(point)
            }
        }

        return generateIndices()
    }

    private fun interiorCase(point: Vector2, triangle: DelaunayTriangle) {
        val (a, b, c) = triangle
        triangles.remove(triangle)

        val first = DelaunayTriangle(a, b, point)
        val second = DelaunayTriangle(b, c, point)
        val third = DelaunayTriangle(c, a, point)

        triangles.add(first)
        triangles.add(second)
        triangles.add(third)

        checkAndLegalize(first.ab, second.ab, third.ab)
    }

    private fun collinearCase(point: Vector2) {
        val first = triangles.find { it.contains(point) == 0 }
        val second = triangles.findLast { it.contains(point) == 0 }
        if (first == null || second == null) {
            throw IllegalStateException("Triangulation failed, point (${point.x}, ${point.y}) is not inside any triangle")
        }

        val edge1 = first.findOneEdge { point.isOnRightOf(it) == 0 }
        val edge2 = second.findOneEdge { point.isOnRightOf(it) == 0 }

        triangles.remove(first)
        triangles.remove(second)

        val triangle1 = DelaunayTriangle(edge1.p1, edge1.opposite, point)
        val triangle2 = DelaunayTriangle(edge1.p2, edge1.opposite, point)
        val triangle3 = DelaunayTriangle(edge1.p1, edge2.opposite, point)
        val triangle4 = DelaunayTriangle(edge1.p2, edge2.opposite, point)
        triangles.add(triangle1)
        triangles.add(triangle2)
        triangles.add(triangle3)
        triangles.add(triangle4)

        checkAndLegalize(triangle1.ab, triangle2.ab, triangle3.ab, triangle4.ab)
    }

    private fun checkAndLegalize(edge: DelaunayEdge) {
        val coincident = edge.findCoincidentEdge() ?: return

        if (!isLegal(edge, coincident.opposite)) {
            legalizeEdge(edge, coincident)
        }
    }

    private fun legalizeEdge(edge: DelaunayEdge, coincident: DelaunayEdge) {
        require(triangles.remove(edge.triangle))
        require(triangles.remove(coincident.triangle))

        val first = DelaunayTriangle(coincident.opposite, edge.p1, edge.opposite)
        val second = DelaunayTriangle(coincident.opposite, edge.p2, edge.opposite)

        triangles.add(first)
        triangles.add(second)

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

    private fun generateIndices(): ShortArray {
        val triangles = triangles.filter { !it.hasSuperPoint }
        var i = 0
        val shorts = ShortArray(triangles.size*3)

        for (triangle in triangles) {
            shorts[i] = points.indexOf(triangle.a).toShort()
            shorts[i+1] = points.indexOf(triangle.b).toShort()
            shorts[i+2] = points.indexOf(triangle.c).toShort()

            i+=3
        }

        return shorts
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


    private fun checkAndLegalize(edge1: DelaunayEdge, edge2: DelaunayEdge) {
        checkAndLegalize(edge1)
        checkAndLegalize(edge2)
    }

    private fun checkAndLegalize(edge1: DelaunayEdge, edge2: DelaunayEdge, edge3: DelaunayEdge) {
        checkAndLegalize(edge1)
        checkAndLegalize(edge2)
        checkAndLegalize(edge3)
    }

    private fun checkAndLegalize(edge1: DelaunayEdge, edge2: DelaunayEdge, edge3: DelaunayEdge, edge4: DelaunayEdge) {
        checkAndLegalize(edge1)
        checkAndLegalize(edge2)
        checkAndLegalize(edge3)
        checkAndLegalize(edge4)
    }
}

