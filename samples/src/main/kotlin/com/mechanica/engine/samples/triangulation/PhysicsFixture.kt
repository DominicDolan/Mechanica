package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import kotlin.math.hypot

/**
 * The jbox2d constants that decide what a set of corners actually becomes once it reaches the
 * physics engine. Values are from `org.jbox2d.common.Settings` in jbox2d 2.2.1.
 */
object Box2d {
    const val MAX_POLYGON_VERTICES = 8

    /** Below this, jbox2d treats lengths as zero. */
    const val LINEAR_SLOP = 0.005

    /** Every polygon fixture is quietly inflated by this much, which fattens the wall for free. */
    const val POLYGON_RADIUS = 2.0 * LINEAR_SLOP
}

/** Something about a fixture that jbox2d will not handle the way you would hope. */
enum class FixtureFault(val message: String) {
    TOO_FEW_POINTS("fewer than 3 corners: jbox2d silently substitutes a 1x1 box"),
    DEGENERATE_HULL("hull collapsed below 3 corners"),
    SHORT_HULL_EDGE("a hull edge is shorter than the linear slop, so its normal comes out NaN"),
    SLIVER("hull area is negligible"),
}

/**
 * What a set of corners becomes once jbox2d has had it.
 *
 * `PolygonShape.set` does not use the polygon you hand it. It runs a gift-wrapping convex hull over
 * the points and keeps that, and it warns that the points may be reordered. So a quad that came out
 * non-convex, or crossed into a bowtie, is quietly replaced by its hull — which covers *more*, and
 * therefore closes exactly the kind of hole that would let something through. That is the single
 * biggest reason the wall builder can afford to be sloppy about convexity.
 *
 * It also means the shape that actually collides is not the shape you drew, which is worth seeing
 * directly rather than trusting. [hull] is what will really be in the world.
 */
class PhysicsFixture(
    /** The corners as handed to `PolygonShape.set`. */
    val requested: List<Vector2>,
    /** The convex hull jbox2d will keep instead. */
    val hull: List<Vector2>,
    val faults: List<FixtureFault>,
) {
    val isValid: Boolean
        get() = faults.isEmpty()

    /** True when jbox2d discarded or moved corners — the drawn shape is not the colliding one. */
    val wasReshaped: Boolean
        get() = hull.size != requested.size

    fun contains(x: Double, y: Double): Boolean {
        if (hull.size < 3) return false
        var negative = false
        var positive = false
        for (i in hull.indices) {
            val a = hull[i]
            val b = hull[(i + 1) % hull.size]
            val side = (b.x - a.x) * (y - a.y) - (b.y - a.y) * (x - a.x)
            if (side < 0.0) negative = true
            if (side > 0.0) positive = true
        }
        return !(negative && positive)
    }
}

/** Runs [toFixture] over every quad in the band. */
fun WallBand.toFixtures(): List<PhysicsFixture> = pieces.map { toFixture(it.corners) }

/**
 * Reproduces `org.jbox2d.collision.shapes.PolygonShape.set` on a set of corners, so a demo can show
 * the shape that will really collide rather than the one that was requested.
 *
 * The hull is gift-wrapped exactly as jbox2d does it: start from the rightmost point, breaking ties
 * on the lowest, and repeatedly take the point that everything else is left of. Collinear points
 * lose to the furthest one along the same line, so they drop out.
 */
fun toFixture(corners: List<Vector2>): PhysicsFixture {
    val faults = ArrayList<FixtureFault>()

    if (corners.size < 3) {
        faults.add(FixtureFault.TOO_FEW_POINTS)
        return PhysicsFixture(corners, emptyList(), faults)
    }

    val points = corners.take(Box2d.MAX_POLYGON_VERTICES)

    // Rightmost point, ties broken by the lowest, exactly as jbox2d picks its start.
    var start = 0
    for (i in points.indices) {
        if (points[i].x > points[start].x || (points[i].x == points[start].x && points[i].y < points[start].y)) {
            start = i
        }
    }

    val hull = ArrayList<Vector2>(points.size)
    var current = start
    while (true) {
        hull.add(points[current])

        var candidate = 0
        for (j in 1 until points.size) {
            if (candidate == current) {
                candidate = j
                continue
            }
            val rX = points[candidate].x - points[current].x
            val rY = points[candidate].y - points[current].y
            val vX = points[j].x - points[current].x
            val vY = points[j].y - points[current].y
            val cross = rX * vY - rY * vX
            if (cross < 0.0) candidate = j
            // Collinear: the further point wins, so points between drop out of the hull.
            if (cross == 0.0 && vX * vX + vY * vY > rX * rX + rY * rY) candidate = j
        }

        current = candidate
        if (current == start || hull.size >= points.size) break
    }

    if (hull.size < 3) {
        faults.add(FixtureFault.DEGENERATE_HULL)
    } else {
        // Duplicate corners drop out of the hull on their own, so what matters is whether any edge
        // that survived is too short — jbox2d normalizes each edge to get its normal, and a
        // zero-length one comes back NaN.
        val shortEdge = hull.indices.any { i ->
            val a = hull[i]
            val b = hull[(i + 1) % hull.size]
            hypot(b.x - a.x, b.y - a.y) < Box2d.LINEAR_SLOP
        }
        if (shortEdge) faults.add(FixtureFault.SHORT_HULL_EDGE)
    }
    if (areaOf(hull) < Box2d.LINEAR_SLOP * Box2d.LINEAR_SLOP) faults.add(FixtureFault.SLIVER)

    return PhysicsFixture(corners, hull, faults)
}

private fun areaOf(polygon: List<Vector2>): Double {
    if (polygon.size < 3) return 0.0
    var total = 0.0
    for (i in polygon.indices) {
        val a = polygon[i]
        val b = polygon[(i + 1) % polygon.size]
        total += a.x * b.y - b.x * a.y
    }
    return kotlin.math.abs(total) / 2.0
}
