package com.mechanica.engine.geometry

import com.cave.library.vector.vec2.Vector2
import kotlin.math.abs

internal const val GEOMETRY_EPSILON = 1e-9

/**
 * Twice the signed area of the closed polygon through [outline], halved: positive when the outline
 * runs counter-clockwise, negative when it runs clockwise.
 *
 * The outline is treated as closed — the last point joins back to the first — so it must not repeat
 * its first point at the end.
 */
fun signedArea(outline: List<Vector2>): Double {
    var total = 0.0
    for (i in outline.indices) {
        val current = outline[i]
        val next = outline[(i + 1) % outline.size]
        total += current.x * next.y - next.x * current.y
    }
    return total / 2.0
}

/** Whether [outline] runs counter-clockwise. Meaningless for an outline that crosses itself. */
fun isCounterClockwise(outline: List<Vector2>) = signedArea(outline) > 0.0

/**
 * Every point where [outline] crosses itself. Empty for a simple polygon.
 *
 * Worth checking before handing an outline to anything that insets or offsets it. Those all work out
 * which side is inside from the winding, and a self-crossing outline has no consistent answer:
 * the winding says one thing while part of the boundary runs the other way, so along that part
 * "inward" is actually outward. The result is wrong rather than merely imprecise, and it fails
 * silently, which makes it look like a bug in the offsetting rather than in its input.
 */
fun selfIntersections(outline: List<Vector2>): List<Vector2> {
    val crossings = ArrayList<Vector2>()
    val count = outline.size

    for (i in 0 until count) {
        for (j in i + 1 until count) {
            // Edges that share a vertex always meet there; that is not a crossing.
            if (j == i + 1 || (i == 0 && j == count - 1)) continue

            val crossing = segmentCrossing(
                outline[i], outline[(i + 1) % count],
                outline[j], outline[(j + 1) % count]
            )
            if (crossing != null) crossings.add(crossing)
        }
    }
    return crossings
}

/**
 * Where two line segments properly cross, or `null` if they don't.
 *
 * "Properly" means strictly inside both: segments that merely touch at an endpoint, or that lie
 * along each other, are not crossings.
 */
fun segmentCrossing(fromA: Vector2, toA: Vector2, fromB: Vector2, toB: Vector2): Vector2? {
    val aX = toA.x - fromA.x
    val aY = toA.y - fromA.y
    val bX = toB.x - fromB.x
    val bY = toB.y - fromB.y

    val denominator = aX * bY - aY * bX
    if (abs(denominator) < GEOMETRY_EPSILON) return null // parallel segments never properly cross

    val offsetX = fromB.x - fromA.x
    val offsetY = fromB.y - fromA.y
    val alongA = (offsetX * bY - offsetY * bX) / denominator
    val alongB = (offsetX * aY - offsetY * aX) / denominator

    if (alongA <= GEOMETRY_EPSILON || alongA >= 1.0 - GEOMETRY_EPSILON) return null
    if (alongB <= GEOMETRY_EPSILON || alongB >= 1.0 - GEOMETRY_EPSILON) return null

    return Vector2.create(fromA.x + alongA * aX, fromA.y + alongA * aY)
}
