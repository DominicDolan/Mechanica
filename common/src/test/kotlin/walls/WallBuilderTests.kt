package walls

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.selfIntersections
import com.mechanica.engine.geometry.signedArea
import com.mechanica.engine.geometry.walls.buildWalls
import org.junit.Test
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Exact facts about [buildWalls] — the ones that can be stated as a number rather than measured.
 *
 * The statistical side of it, how much of a shape the wall covers and how much it spills, is not
 * here: that needs sampling over whole shapes and lives in the `PhysicsWallDemo` harness in the
 * samples module, which prints it as a table to compare against after a change.
 */
class WallBuilderTests {

    private fun p(x: Double, y: Double) = Vector2.create(x, y)

    private val square = listOf(p(-2.0, -2.0), p(2.0, -2.0), p(2.0, 2.0), p(-2.0, 2.0))

    /** The L's reflex corner is at the origin. */
    private val lShape =
        listOf(p(-2.5, -2.5), p(2.5, -2.5), p(2.5, 0.0), p(0.0, 0.0), p(0.0, 2.5), p(-2.5, 2.5))

    @Test
    fun `a square inset by half gives one quad per edge and a smaller square inside`() {
        val wall = buildWalls(square, 0.5)

        assertEquals(4, wall.quads.size, "one quad per edge")
        assertTrue(wall.caps.isEmpty(), "a convex outline needs no corner caps")
        assertEquals(0, wall.limitedCount, "no corner is tight enough to cut short")

        // 4x4 inset by 0.5 on every side is 3x3, so every corner sits at (+-1.5, +-1.5).
        assertTrue(
            wall.inner.all { abs(abs(it.x) - 1.5) < 1e-9 && abs(abs(it.y) - 1.5) < 1e-9 },
            "expected a 3x3 inner square, got ${wall.inner.map { "(${it.x}, ${it.y})" }}"
        )
    }

    @Test
    fun `a reflex corner miters to the intersection of both inset edges`() {
        val reflex = buildWalls(lShape, 0.4).inner[3]

        // Both edges at the origin are axis-aligned, so the point 0.4 from both is (-0.4, -0.4).
        assertTrue(
            abs(reflex.x + 0.4) < 1e-9 && abs(reflex.y + 0.4) < 1e-9,
            "expected (-0.4, -0.4), got (${reflex.x}, ${reflex.y})"
        )
    }

    @Test
    fun `a reflex corner gets caps and a convex one does not`() {
        assertTrue(buildWalls(square, 0.4).caps.isEmpty(), "a square has no reflex corners")
        assertTrue(buildWalls(lShape, 0.4).caps.isNotEmpty(), "the L's inside corner needs bridging")
    }

    @Test
    fun `winding does not change the wall`() {
        val clockwise = square.reversed()
        assertTrue(signedArea(clockwise) < 0.0, "this fixture is meant to be wound clockwise")

        val wall = buildWalls(clockwise, 0.5)
        assertEquals(0, wall.limitedCount)
        assertTrue(
            wall.inner.all { abs(abs(it.x) - 1.5) < 1e-9 && abs(abs(it.y) - 1.5) < 1e-9 },
            "clockwise input should give the same 3x3 inner square"
        )
    }

    @Test
    fun `a needle is capped by the miter limit rather than running away`() {
        // The tip is so sharp that its true miter point is effectively at infinity.
        val needle = listOf(p(-3.0, 0.0), p(3.0, -0.05), p(3.0, 0.05))
        val miterLimit = 2.5
        val thickness = 0.5

        val wall = buildWalls(needle, thickness, miterLimit)

        assertTrue(wall.inner.all { it.x.isFinite() && it.y.isFinite() }, "corners must stay finite")
        wall.inner.forEachIndexed { i, corner ->
            val reach = hypot(corner.x - needle[i].x, corner.y - needle[i].y)
            assertTrue(
                reach <= miterLimit * thickness + 1e-9,
                "corner $i reached $reach, past the limit of ${miterLimit * thickness}"
            )
        }
    }

    @Test
    fun `an edge is broken where the polygon reaches into it`() {
        // A spike hanging down from the top, stopping 0.3 above the bottom edge. A wall thicker
        // than that gap has the spike's tip sitting inside the bottom edge's slab, so the slab has
        // to step around it instead of running straight through the spike and out the far side.
        val spike = listOf(
            p(-3.0, -2.0), p(3.0, -2.0), p(3.0, 2.0),
            p(0.2, 2.0), p(0.0, -1.7), p(-0.2, 2.0),
            p(-3.0, 2.0)
        )

        assertEquals(
            spike.size, buildWalls(spike, 0.1).quads.size,
            "a wall that stops short of the tip needs no break: one quad per edge"
        )
        assertTrue(
            buildWalls(spike, 0.5).quads.size > spike.size,
            "a wall reaching past the tip must break the bottom edge around it"
        )
    }

    @Test
    fun `every piece has four corners`() {
        val wall = buildWalls(lShape, 0.4)
        assertTrue(wall.pieces.isNotEmpty())
        wall.pieces.forEach { assertEquals(4, it.corners.size) }
    }

    @Test
    fun `degenerate input gives an empty wall rather than throwing`() {
        assertTrue(buildWalls(listOf(p(0.0, 0.0), p(1.0, 0.0)), 0.5).pieces.isEmpty(), "two vertices")
        assertTrue(buildWalls(square, 0.0).pieces.isEmpty(), "zero thickness")
        assertTrue(buildWalls(square, -1.0).pieces.isEmpty(), "negative thickness")
    }

    @Test
    fun `self intersection is detected, since it silently invalidates the wall`() {
        assertTrue(selfIntersections(square).isEmpty())
        assertTrue(selfIntersections(lShape).isEmpty())

        val crossed = listOf(p(-2.0, -2.0), p(2.0, -2.0), p(-2.0, 2.0), p(2.0, 2.0))
        assertFalse(selfIntersections(crossed).isEmpty(), "this outline crosses itself")
    }
}
