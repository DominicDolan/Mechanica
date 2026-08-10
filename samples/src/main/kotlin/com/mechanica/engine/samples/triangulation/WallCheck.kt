package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.isCounterClockwise
import com.mechanica.engine.geometry.selfIntersections
import com.mechanica.engine.geometry.walls.DEFAULT_MITER_LIMIT
import com.mechanica.engine.geometry.walls.buildWalls
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Headless checks for [buildWalls]. Needs no GL context — run `main` straight off the compiled
 * classes.
 *
 * Everything here measures the [PhysicsFixture]s rather than the quads, because the hull is what
 * ends up in the physics world and the quad is not. The assertions are exact facts that must hold.
 * The sweeps after them measure the two ways the wall can be wrong and print the numbers instead of
 * asserting them, since the whole design deliberately trades one against the other:
 *
 *  - a **gap** is boundary the wall failed to cover, and is the bug being fixed — something thin
 *    crosses the surface there;
 *  - a **leak** is wall outside the outline, which is collision in open air.
 *
 * Gaps are much the worse of the two, which is why the builder is tuned to over-cover.
 */
private fun p(x: Double, y: Double) = Vector2.create(x, y)

/** Traced off a screenshot: a thin dogleg, two needles meeting at a pinched waist. */
private val dogleg = listOf(p(-0.87, -1.48), p(5.82, -4.49), p(0.64, -1.66), p(-5.82, 3.79))

private fun insidePolygon(polygon: List<Vector2>, x: Double, y: Double): Boolean {
    var result = false
    for (i in polygon.indices) {
        val a = polygon[i]
        val b = polygon[(i + 1) % polygon.size]
        if ((a.y > y) != (b.y > y)) {
            val crossX = a.x + (y - a.y) / (b.y - a.y) * (b.x - a.x)
            if (x < crossX) result = !result
        }
    }
    return result
}

/** Distance from a point to the nearest edge of [polygon], boundary included. */
private fun distanceToBoundary(polygon: List<Vector2>, x: Double, y: Double): Double {
    var nearest = Double.POSITIVE_INFINITY
    for (i in polygon.indices) {
        val a = polygon[i]
        val b = polygon[(i + 1) % polygon.size]
        val edgeX = b.x - a.x
        val edgeY = b.y - a.y
        val lengthSquared = edgeX * edgeX + edgeY * edgeY
        val along =
            if (lengthSquared < 1e-18) 0.0
            else (((x - a.x) * edgeX + (y - a.y) * edgeY) / lengthSquared).coerceIn(0.0, 1.0)
        val distance = hypot(x - (a.x + along * edgeX), y - (a.y + along * edgeY))
        if (distance < nearest) nearest = distance
    }
    return nearest
}

private class Coverage(val gaps: Int, val leaks: Int)

/**
 * Samples the polygon's bounding box and counts both failure modes against the fixtures.
 *
 * A gap is an interior point within [thickness] of the outline that no fixture covers. Points
 * deeper than that are the hollow middle and are meant to be uncovered. A leak is the reverse — a
 * point outside the outline that a fixture does cover.
 */
private fun coverageOf(
    outline: List<Vector2>,
    fixtures: List<PhysicsFixture>,
    thickness: Double,
    samples: Int = 400
): Coverage {
    val minX = outline.minOf { it.x }
    val maxX = outline.maxOf { it.x }
    val minY = outline.minOf { it.y }
    val maxY = outline.maxOf { it.y }

    // Points a hair either side of a boundary are a coin flip; only count a clear miss.
    val tolerance = 0.02 * thickness
    val slack = 1e-6 * maxOf(maxX - minX, maxY - minY)

    var gaps = 0
    var leaks = 0

    for (ix in 0 until samples) {
        for (iy in 0 until samples) {
            val x = minX + (maxX - minX) * (ix + 0.5) / samples
            val y = minY + (maxY - minY) * (iy + 0.5) / samples
            val covered = fixtures.any { it.contains(x, y) }

            if (!insidePolygon(outline, x, y)) {
                if (covered && distanceToBoundary(outline, x, y) > slack) leaks++
                continue
            }
            if (!covered && distanceToBoundary(outline, x, y) <= thickness - tolerance) gaps++
        }
    }
    return Coverage(gaps, leaks)
}

private fun coverageOf(outline: List<Vector2>, thickness: Double, miterLimit: Double) =
    coverageOf(outline, buildWalls(outline, thickness, miterLimit).toFixtures(), thickness)

/**
 * The thinnest the wall gets anywhere along the outline, as a fraction of [thickness].
 *
 * This is the measurement that actually answers the question the walls exist for. A body crosses a
 * surface by getting from one side to the other in a single step, so what matters is how deep the
 * wall runs *at the boundary* — not whether every interior point got filled. A hollow patch deep
 * inside a spike narrower than the body is unreachable without crossing the wall first, and the
 * volumetric count above flags it anyway; this does not.
 */
private fun thinnestWall(outline: List<Vector2>, fixtures: List<PhysicsFixture>, thickness: Double): Double {
    val ring = if (isCounterClockwise(outline)) outline else outline.reversed()
    val steps = 24
    var thinnest = 1.0

    for (i in ring.indices) {
        val from = ring[i]
        val to = ring[(i + 1) % ring.size]
        val length = hypot(to.x - from.x, to.y - from.y)
        if (length < 1e-9) continue

        val normalX = -(to.y - from.y) / length
        val normalY = (to.x - from.x) / length
        val samples = maxOf(4, (length / thickness * 4).toInt().coerceAtMost(200))

        for (s in 0 until samples) {
            val along = (s + 0.5) / samples
            val x = from.x + (to.x - from.x) * along
            val y = from.y + (to.y - from.y) * along

            var covered = 0.0
            var available = 0.0
            for (step in 1..steps) {
                val distance = thickness * step / steps
                val probeX = x + normalX * distance
                val probeY = y + normalY * distance
                // Where the polygon is thinner than the wall, running out of polygon is as deep as
                // the wall could possibly go, and is not a thin spot.
                if (!insidePolygon(outline, probeX, probeY)) break
                available = distance
                if (fixtures.none { it.contains(probeX, probeY) }) break
                covered = distance
            }
            if (available > 0.0 && covered / available < thinnest) thinnest = covered / available
        }
    }
    return thinnest
}

fun main() {
    var failures = 0
    fun check(name: String, condition: Boolean, detail: String = "") {
        if (!condition) {
            failures++
            println("FAIL  $name  $detail")
        } else println("ok    $name")
    }

    // The exact facts about buildWalls itself — corner positions, miter limits, where an edge
    // gets broken — are unit tested next to it, in common's WallBuilderTests. What is here is
    // everything that needs whole shapes and a lot of sampling to say anything about.

    // The precondition every other measurement here rests on.
    for (testCase in testPolygons + TestPolygon("Dogleg", dogleg)) {
        val crossings = selfIntersections(testCase.outline)
        check("${testCase.name}: outline does not cross itself", crossings.isEmpty(), "${crossings.size} crossings")
    }
    // And the detector has to actually catch one, or the check above proves nothing.
    val crossed = listOf(p(-2.0, -2.0), p(2.0, -2.0), p(3.0, 0.0), p(-2.5, 2.0), p(2.5, 2.0), p(-3.0, 0.0))
    check("a crossed outline is detected", selfIntersections(crossed).isNotEmpty())

    // Nothing may reach the physics engine that it cannot build a real polygon out of.
    for (testCase in testPolygons + TestPolygon("Dogleg", dogleg)) {
        for (thickness in listOf(0.05, 0.2, 1.0, 3.0)) {
            val fixtures = buildWalls(testCase.outline, thickness).toFixtures()
            val faulty = fixtures.filter { !it.isValid }
            check(
                "${testCase.name} @ $thickness: every fixture is buildable",
                faulty.isEmpty(),
                faulty.joinToString { it.faults.joinToString() }
            )
        }
    }

    // An outline with detail finer than jbox2d's own slop cannot produce a sound fixture, and the
    // fault detector has to say so rather than let a NaN normal into the world.
    val subSlop = buildWalls(listOf(p(-3.0, 0.0), p(3.0, -0.0005), p(3.0, 0.0005)), 0.05).toFixtures()
    check(
        "sub-slop outline is reported, not passed through",
        subSlop.any { !it.isValid },
        "every fixture claimed to be fine"
    )

    // Regression: the pinched waist that used to tear a hole clean through the wall. Measured at
    // the boundary, which is what decides whether anything can cross it. The bar is deliberately
    // "no hole" rather than "full depth" — the table below shows this one still thins to about
    // three quarters at the smallest thicknesses, which is a weak spot and not a way through.
    for (thickness in listOf(0.2, 0.4, 0.6, 1.0, 2.0, 6.0)) {
        val thinnest = thinnestWall(dogleg, buildWalls(dogleg, thickness).toFixtures(), thickness)
        check(
            "dogleg @ $thickness: no way through the waist",
            thinnest > 0.7,
            "thinnest point is %.0f%% of the wall".format(thinnest * 100)
        )
    }

    println(if (failures == 0) "\nAll assertions passed." else "\n$failures ASSERTION FAILURES")

    val cases = testPolygons.map { it.name to it.outline } + listOf("Dogleg" to dogleg)
    val thicknesses = listOf(0.2, 0.4, 0.8, 1.5, 3.0)

    println("\nThinnest point of the wall along the outline, as a fraction of the wall thickness.")
    println("This is the tunnelling measure: 100% means the wall is full depth everywhere.")
    println("%-20s %9s %9s %9s %9s %9s".format("case", *thicknesses.map { "t=$it" }.toTypedArray()))
    for ((name, outline) in cases) {
        val cells = thicknesses.map { thickness ->
            val thinnest = thinnestWall(outline, buildWalls(outline, thickness).toFixtures(), thickness)
            "%.0f%%".format(thinnest * 100)
        }
        println("%-20s %9s %9s %9s %9s %9s".format(name, *cells.toTypedArray()))
    }

    println("\nPer case at miter limit $DEFAULT_MITER_LIMIT (gaps/leaks, out of 160k samples, - is clean)")
    println("%-20s %9s %9s %9s %9s %9s".format("case", *thicknesses.map { "t=$it" }.toTypedArray()))
    for ((name, outline) in cases) {
        val cells = thicknesses.map { thickness ->
            val c = coverageOf(outline, thickness, DEFAULT_MITER_LIMIT)
            if (c.gaps == 0 && c.leaks == 0) "-" else "${c.gaps}/${c.leaks}"
        }
        println("%-20s %9s %9s %9s %9s %9s".format(name, *cells.toTypedArray()))
    }

    // The dial. Gaps are the bug; leaks are the price of closing them.
    println("\nMiter limit trade-off, totalled over every case and thickness")
    println("%12s %10s %10s".format("miter limit", "gaps", "leaks"))
    for (limit in listOf(1.0, 1.5, 2.0, 2.5, 4.0, 8.0)) {
        var gaps = 0
        var leaks = 0
        for ((_, outline) in cases) {
            for (thickness in thicknesses) {
                val c = coverageOf(outline, thickness, limit)
                gaps += c.gaps
                leaks += c.leaks
            }
        }
        println("%12.1f %10d %10d".format(limit, gaps, leaks))
    }
}
