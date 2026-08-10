package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.isCounterClockwise
import com.mechanica.engine.geometry.shapes.Triangle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A named polygon outline used as a triangulation test case.
 *
 * Every outline is a simple polygon: no self intersections and no holes, so a correct
 * triangulation of one always has exactly `outline.size - 2` triangles. The outline is
 * counter-clockwise unless the case exists specifically to test the clockwise path.
 */
class TestPolygon(val name: String, outline: List<Vector2>, clockwise: Boolean = false) {

    val outline: List<Vector2> =
        if (isCounterClockwise(outline) != clockwise) outline else outline.reversed()

    /** Number of triangles a correct triangulation of this outline must produce. */
    val expectedTriangleCount: Int
        get() = outline.size - 2
}

/** All test cases, in the order the demo cycles through them. */
val testPolygons: List<TestPolygon> = listOf(
    TestPolygon("Square", points(-2.5,-2.5,  2.5,-2.5,  2.5,2.5,  -2.5,2.5)),
    TestPolygon("Convex hexagon", regularPolygon(sides = 6, radius = 3.0)),
    // One reflex vertex, at the inside corner.
    TestPolygon("L-shape", points(-2.5,-2.5,  2.5,-2.5,  2.5,0.0,  0.0,0.0,  0.0,2.5,  -2.5,2.5)),
    // Reflex and convex vertices strictly alternating.
    TestPolygon("Star", star(points = 5, outer = 3.2, inner = 1.3)),
    // The classic ear-clipping stress case: every tooth is a separate pocket.
    TestPolygon("Comb", comb(teeth = 5)),
    // A long chain of reflex vertices; no two ears are ever adjacent.
    TestPolygon("Spiral", spiral()),
    // Collinear runs along the bottom and the right edge: no vertex there spans any area.
    TestPolygon("Collinear edges", points(-3.0,-2.0, -1.0,-2.0,  1.0,-2.0,  3.0,-2.0,  3.0,0.0,  3.0,2.0,  0.0,2.0,  -3.0,2.0,  -3.0,0.0)),
    // A near-degenerate wedge that almost splits the polygon in two.
    TestPolygon("Sliver", points(-3.0,-1.5,  3.0,-1.5,  3.0,-1.0,  -2.8,-0.88,  3.0,-0.4,  3.0,1.5,  -3.0,1.5)),
    // Same shape as the hexagon, wound the other way.
    TestPolygon("Clockwise hexagon", regularPolygon(sides = 6, radius = 3.0), clockwise = true),
)

/** Builds a vertex list from flat `x, y, x, y, ...` coordinates. */
private fun points(vararg xy: Double): List<Vector2> {
    require(xy.size % 2 == 0) { "Expected an even number of coordinates, got ${xy.size}" }
    return List(xy.size / 2) { Vector2.create(xy[it * 2], xy[it * 2 + 1]) }
}

private fun regularPolygon(sides: Int, radius: Double) = List(sides) { i ->
    val angle = 2.0 * PI * i / sides
    Vector2.create(radius * cos(angle), radius * sin(angle))
}

private fun star(points: Int, outer: Double, inner: Double) = List(points * 2) { i ->
    val radius = if (i % 2 == 0) outer else inner
    val angle = PI / 2.0 + i * PI / points
    Vector2.create(radius * cos(angle), radius * sin(angle))
}

private fun comb(
    teeth: Int,
    width: Double = 6.0,
    bottom: Double = -2.5,
    top: Double = 2.5,
    notchDepth: Double = 3.5
): List<Vector2> {
    val left = -width / 2.0
    val segment = width / (2 * teeth + 1)
    val notchY = top - notchDepth

    // The notched edge, walked left to right.
    val topEdge = ArrayList<Vector2>()
    topEdge.add(Vector2.create(left, top))
    for (i in 0 until teeth) {
        val notchLeft = left + (2 * i + 1) * segment
        val notchRight = notchLeft + segment
        topEdge.add(Vector2.create(notchLeft, top))
        topEdge.add(Vector2.create(notchLeft, notchY))
        topEdge.add(Vector2.create(notchRight, notchY))
        topEdge.add(Vector2.create(notchRight, top))
    }
    topEdge.add(Vector2.create(left + width, top))

    // Counter-clockwise: along the flat bottom, then back along the notched top.
    return listOf(Vector2.create(left, bottom), Vector2.create(left + width, bottom)) + topEdge.reversed()
}

private fun spiral(
    turns: Double = 1.75,
    innerRadius: Double = 0.9,
    outerRadius: Double = 3.0,
    band: Double = 0.55,
    steps: Int = 40
): List<Vector2> {
    // `band` must stay under the radial pitch, or the arm overlaps itself and the polygon
    // stops being simple.
    fun arm(inset: Double) = List(steps + 1) { i ->
        val t = i.toDouble() / steps
        val angle = t * turns * 2.0 * PI
        val radius = innerRadius + (outerRadius - innerRadius) * t - inset
        Vector2.create(radius * cos(angle), radius * sin(angle))
    }

    return arm(inset = 0.0) + arm(inset = band).reversed()
}
