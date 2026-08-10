package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.scenes.scenes.Scene
import kotlin.math.hypot

fun main() {
    Game.configure {
        setFullscreen(false)
        setViewport(height = 10.0)
        setStartingScene { PhysicsWallDemo() }
    }

    Game.loop()
}

/**
 * A harness for [buildWalls], showing what the wall becomes once a physics engine has it.
 *
 * The filled shapes are not the quads the builder produced — they are the convex hulls jbox2d will
 * replace those quads with, which is the geometry that actually collides. Anything drawn here that
 * spills past the green outline is collision in open air; anything the depth probe marks red is
 * wall too thin to stop something crossing it, which is the bug this is all for.
 *
 * The two controls trade those against each other. Thickness sets how deep the wall should be;
 * the miter limit caps how far a tight corner may reach, so lowering it spills less and risks
 * gaps, and raising it covers more and spills more.
 *
 * Controls:
 *   left / right  cycle test polygons
 *   scroll        wall thickness
 *   [ / ]         miter limit
 *   drag MB1      move a vertex
 *   MB2           delete a vertex
 *   R             reset the current polygon
 *   I             vertex index labels
 *   W             wireframe over the fixtures
 *   D             depth probe
 */
class PhysicsWallDemo : Scene(), Inputs by Inputs.create() {

    private val handleRadius = 0.2

    private var polygonIndex = 0
    private val testCase: TestPolygon
        get() = testPolygons[polygonIndex]

    /** Working copy of the current test case, so dragging never edits the source outline. */
    private val vertices = ArrayList<Vector2>()

    private var thickness = 0.4
    private var miterLimit = DEFAULT_MITER_LIMIT

    private var wall = WallBand.empty
    private var fixtures: List<PhysicsFixture> = emptyList()
    private var probe: List<DepthSample> = emptyList()

    /** Dragging a vertex across another edge is easy to do by accident and invalidates everything. */
    private var crossings: List<Vector2> = emptyList()

    private val batch = TriangleBatch(triangleCapacity = 2048)
    private var wireframe: List<Array<Vector2>> = emptyList()

    private var draggedIndex = -1
    private var showIndices = false
    private var showWireframe = true
    private var showProbe = true

    /** How deep the wall runs at one point on the outline, as a fraction of the wall thickness. */
    private class DepthSample(val at: Vector2, val depth: Double, val ratio: Double)

    init {
        load(0)
    }

    override fun update(delta: Double) {
        if (keyboard.right.hasBeenPressed) load(polygonIndex + 1)
        if (keyboard.left.hasBeenPressed) load(polygonIndex - 1)
        if (keyboard.R.hasBeenPressed) load(polygonIndex)
        if (keyboard.I.hasBeenPressed) showIndices = !showIndices
        if (keyboard.W.hasBeenPressed) showWireframe = !showWireframe
        if (keyboard.D.hasBeenPressed) showProbe = !showProbe

        if (mouse.scrollUp.hasBeenPressed) setThickness(thickness + 0.05)
        if (mouse.scrollDown.hasBeenPressed) setThickness(thickness - 0.05)
        if (keyboard.rightBracket.hasBeenPressed) setMiterLimit(miterLimit + 0.5)
        if (keyboard.leftBracket.hasBeenPressed) setMiterLimit(miterLimit - 0.5)

        handleMouse()
    }

    override fun render(draw: Drawer) {
        // The collision geometry as jbox2d will hold it, not as the builder drew it.
        draw.color(0x2E7D9AFF).model(batch.model)

        if (showWireframe) {
            wireframe.forEach { draw.color(0x9AD8F0FF).stroke(0.012).path(it) }
        }

        // The outline last, so a fixture spilling past it reads immediately as collision in air.
        draw.green.stroke(0.035).path(closedPath(vertices))

        if (showProbe) renderProbe(draw)

        crossings.forEach { draw.color(0xFF2020FF).circle(it, 0.16) }

        vertices.forEachIndexed { i, vertex ->
            val focused = i == draggedIndex || i == vertexUnderMouse()
            val color = when {
                focused -> 0xFFFFFFFF
                wall.limited.getOrElse(i) { false } -> 0xFFB74DFF
                else -> 0x00C000FF
            }
            draw.color(color).circle(vertex, handleRadius)
            if (showIndices) {
                draw.lightGrey.text("$i", 0.22, vertex.x + handleRadius, vertex.y + handleRadius)
            }
        }

        renderHud(draw)
    }

    /** Each probe sample as a stub running inwards, as long as the wall is deep at that point. */
    private fun renderProbe(draw: Drawer) {
        probe.forEach { sample ->
            val color = when {
                sample.ratio < 0.25 -> 0xFF2020FF
                sample.ratio < 0.75 -> 0xFFB74DFF
                else -> 0x40C040FF
            }
            draw.color(color).circle(sample.at, 0.045)
        }
    }

    private fun renderHud(draw: Drawer) {
        var y = Game.ui.top - 0.45
        fun line(text: String, color: Long) {
            draw.ui.color(color).text(text, 0.3, Game.ui.left + 0.3, y)
            y -= 0.4
        }

        val faulty = fixtures.count { !it.isValid }
        val reshaped = fixtures.count { it.wasReshaped }
        val worst = probe.minByOrNull { it.ratio }

        line("[${polygonIndex + 1}/${testPolygons.size}] ${testCase.name}", 0xFFFFFFFF)
        // Worth shouting about: with a crossed outline there is no consistent inside, so every
        // number below it is meaningless and the wall builds itself into open air.
        if (crossings.isNotEmpty()) {
            line("OUTLINE CROSSES ITSELF at ${crossings.size} point(s) — results below are meaningless", 0xFF2020FF)
        }
        line(
            "${vertices.size} vertices   thickness ${"%.2f".format(thickness)}   miter limit ${"%.1f".format(miterLimit)}",
            0xB0B0B0FF
        )
        line("${wall.quads.size} edge quads + ${wall.caps.size} corner caps, ${wall.limitedCount} corners cut short", 0xB0B0B0FF)
        // A reshaped fixture is jbox2d hulling away a non-convex or crossed quad. Expected in
        // pinched regions, and the reason those do not become holes.
        line("$reshaped fixtures reshaped by the hull", if (reshaped == 0) 0x808080FF else 0x9AD8F0FF)
        line(
            if (faulty == 0) "no degenerate fixtures" else "$faulty DEGENERATE FIXTURES",
            if (faulty == 0) 0x00C000FF else 0xFF2020FF
        )
        if (worst != null) {
            val depth = "thinnest wall ${"%.3f".format(worst.depth)} of ${"%.2f".format(thickness)}"
            line(
                if (worst.ratio < 0.25) "$depth  <- can be crossed here" else depth,
                if (worst.ratio < 0.25) 0xFF2020FF else if (worst.ratio < 0.75) 0xFFB74DFF else 0x00C000FF
            )
        }
        line("arrows: polygon   scroll: thickness   [ ]: miter limit   MB1/MB2   R I W D", 0x808080FF)
    }

    /**
     * Walks inwards from points spread along the outline and records how far the collision
     * geometry actually reaches before it runs out.
     *
     * This is the question the whole exercise is about. A thin patch is somewhere a body can cross
     * the surface in one step, and it is not something the eye picks out of a filled shape, so it
     * gets measured against the fixtures rather than the quads.
     */
    private fun measureDepth(): List<DepthSample> {
        if (vertices.size < 3 || fixtures.isEmpty()) return emptyList()

        val ring = if (isCounterClockwise(vertices)) vertices else vertices.reversed()
        val perimeter = ring.indices.sumOf { i ->
            val a = ring[i]
            val b = ring[(i + 1) % ring.size]
            hypot(b.x - a.x, b.y - a.y)
        }
        if (perimeter <= 0.0) return emptyList()

        val totalSamples = 160
        val steps = 12
        val samples = ArrayList<DepthSample>(totalSamples)

        for (i in ring.indices) {
            val from = ring[i]
            val to = ring[(i + 1) % ring.size]
            val edgeLength = hypot(to.x - from.x, to.y - from.y)
            if (edgeLength < 1e-9) continue

            val normalX = -(to.y - from.y) / edgeLength
            val normalY = (to.x - from.x) / edgeLength
            val perEdge = maxOf(2, (totalSamples * edgeLength / perimeter).toInt())

            for (s in 0 until perEdge) {
                val along = (s + 0.5) / perEdge
                val x = from.x + (to.x - from.x) * along
                val y = from.y + (to.y - from.y) * along

                // Walk in until the geometry stops covering us, and call that the depth here.
                var depth = 0.0
                for (step in 1..steps) {
                    val distance = thickness * step / steps
                    val probeX = x + normalX * distance
                    val probeY = y + normalY * distance
                    if (fixtures.none { it.contains(probeX, probeY) }) break
                    depth = distance
                }

                samples.add(DepthSample(Vector2.create(x, y), depth, depth / thickness))
            }
        }
        return samples
    }

    private fun handleMouse() {
        if (mouse.MB1.hasBeenPressed) draggedIndex = vertexUnderMouse()

        if (!mouse.MB1()) {
            draggedIndex = -1
        } else if (draggedIndex >= 0) {
            vertices[draggedIndex] = Vector2.create(mouse.world.x, mouse.world.y)
            rebuild()
        }

        if (mouse.MB2.hasBeenPressed) {
            val index = vertexUnderMouse()
            if (index >= 0 && vertices.size > 3) {
                vertices.removeAt(index)
                rebuild()
            }
        }
    }

    private fun vertexUnderMouse(): Int {
        for (i in vertices.indices) {
            val vertex = vertices[i]
            if (hypot(vertex.x - mouse.world.x, vertex.y - mouse.world.y) < handleRadius) return i
        }
        return -1
    }

    private fun setThickness(value: Double) {
        thickness = value.coerceIn(0.05, 4.0)
        rebuild()
    }

    private fun setMiterLimit(value: Double) {
        miterLimit = value.coerceIn(1.0, 10.0)
        rebuild()
    }

    private fun load(index: Int) {
        polygonIndex = (index + testPolygons.size) % testPolygons.size
        draggedIndex = -1

        vertices.clear()
        // Copy, so dragging a vertex doesn't mutate the shared test case.
        testCase.outline.mapTo(vertices) { Vector2.create(it.x, it.y) }

        rebuild()
    }

    private fun rebuild() {
        crossings = selfIntersections(vertices)
        wall = buildWalls(vertices, thickness, miterLimit)
        fixtures = wall.toFixtures()
        probe = measureDepth()

        // Draw the hulls, not the quads: the hull is what will be in the physics world.
        batch.fill(fixtures.flatMap { fanOf(it.hull) })
        wireframe = fixtures.filter { it.hull.size >= 3 }.map { closedPath(it.hull) }
    }

    /** A convex polygon as a triangle fan. Only valid because a hull is convex by construction. */
    private fun fanOf(hull: List<Vector2>): List<Triangle> =
        if (hull.size < 3) emptyList()
        else (1 until hull.size - 1).map { Triangle(hull[0], hull[it], hull[it + 1]) }

    /** A path that returns to its first point, which is what [Drawer.path] needs to close a loop. */
    private fun closedPath(vertices: List<Vector2>) =
        Array(vertices.size + 1) { vertices[it % vertices.size] }
}
